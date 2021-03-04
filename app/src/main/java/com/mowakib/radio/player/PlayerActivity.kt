package com.mowakib.radio.player

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.mowakib.radio.BuildConfig
import com.mowakib.radio.R
import com.mowakib.radio.database.FavDatabaseRadio
import com.mowakib.radio.database.RadiosDatabase
import com.mowakib.radio.database.getRadioDatabase
import com.mowakib.radio.player.DemoUtil.buildRenderersFactory
import com.mowakib.radio.player.DemoUtil.getDataSourceFactory
import com.mowakib.radio.player.DemoUtil.getDownloadTracker
import com.mowakib.radio.player.IntentUtil.createMediaItemsFromIntent
import java.util.*


class PlayerActivity : AppCompatActivity(), PlayerNotificationManager.NotificationListener,
    View.OnClickListener {
    private lateinit var playerView: StyledPlayerView
    private var player: SimpleExoPlayer? = null
    private var mediaItems: List<MediaItem>? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private var startAutoPlay = false
    private var startWindow = 0
    private var startPosition: Long = 0

    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var notificationManager: NotificationManager

    private lateinit var database: RadiosDatabase
    private lateinit var fav: MaterialCheckBox

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSourceFactory = getDataSourceFactory( /* context= */this)
        setContentView(R.layout.player_activity)
        playerView = findViewById(R.id.player_view)
        playerView.setErrorMessageProvider(PlayerErrorMessageProvider())
        playerView.requestFocus()
        playerView.showController()
        if (savedInstanceState != null) {
            trackSelectorParameters =
                savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS)
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow = savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            val builder = ParametersBuilder( /* context= */this)
            trackSelectorParameters = builder.build()
            clearStartPosition()
        }

        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(this,
            CHANNEL_ID,
            R.string.app_name,
            0,
            NOTIFICATION_ID,
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return getRadioName()
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    return null
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return null
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return BitmapFactory.decodeResource(resources, R.drawable.notif_bg)
                }
            })
        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setUseStopAction(true)
        val dispatcher = DefaultControlDispatcher(0, 0)
        playerNotificationManager.setControlDispatcher(dispatcher)

        database = getRadioDatabase(applicationContext)

        findViewById<ImageView>(R.id.exo_close).setOnClickListener(this)
        findViewById<TextView>(R.id.radio_title).text = getRadioName()
        fav = findViewById(R.id.fav)

        val logo = intent.getStringExtra(EXTRA_LOGO)!!
        val name = intent.getStringExtra(EXTRA_NAME)!!
        val url = intent.getStringExtra(EXTRA_URL)!!

        lifecycleScope.launch(Dispatchers.IO) {
            val isFav = database.radioDao.isFav(logo)
            if (isFav) fav.isChecked = true

            val most = database.radioDao.getMost(logo)
            val favRadiosDatabase = FavDatabaseRadio(name, logo, url, most)

//            if (most.toInt() != 0)
//                database.radioDao.update(favRadiosDatabase)
//                Log.d(TAG, "onCreate: => ${most+1}")

            Log.d(TAG, "onCreate: $most")
            addToFav(favRadiosDatabase)
        }
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
//        if (dismissedByUser){
            onDestroy()
//        }
    }

    private fun getRadioName() = intent.getStringExtra(EXTRA_NAME)!!

    private fun addToFav(favRadio: FavDatabaseRadio) {
        fav.setOnCheckedChangeListener(null)
        fav.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (fav.isChecked) {
                    database.radioDao.insert(
                        FavDatabaseRadio(
                            favRadio.name,
                            favRadio.logo,
                            favRadio.url,
                            0,
                        )
                    )
                } else {
                    database.radioDao.delete(
                        FavDatabaseRadio(
                            favRadio.name,
                            favRadio.logo,
                            favRadio.url,
                            favRadio.most,
                        )
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        releasePlayer()
        clearStartPosition()
        setIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        notificationManager.cancelAll()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateTrackSelectorParameters()
        updateStartPosition()
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters)
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState.putInt(KEY_WINDOW, startWindow)
        outState.putLong(KEY_POSITION, startPosition)
    }

    // Activity input
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    private fun initializePlayer() {

        notificationManager.cancel(NOTIFICATION_ID)
        if (player == null) {

            mediaItems = createMediaItems(intent)
            if (mediaItems!!.isEmpty()) {
                return
            }
            val preferExtensionDecoders =
                intent.getBooleanExtra(IntentUtil.PREFER_EXTENSION_DECODERS_EXTRA, false)
            val renderersFactory =
                buildRenderersFactory( /* context= */this, preferExtensionDecoders)
            val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(
                dataSourceFactory!!
            )
                .setAdViewProvider(playerView)
            trackSelector = DefaultTrackSelector( /* context= */this)
            trackSelector!!.parameters = trackSelectorParameters!!
            lastSeenTrackGroupArray = null
            player = SimpleExoPlayer.Builder( /* context= */this, renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector!!)
                .build()
            player!!.addListener(PlayerEventListener())
            player!!.addAnalyticsListener(EventLogger(trackSelector))
            player!!.setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */true)
            player!!.playWhenReady = startAutoPlay
            playerView.player = player

        }
        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player!!.seekTo(startWindow, startPosition)
        }
        player!!.setMediaItems(mediaItems!!,  /* resetPosition= */!haveStartPosition)
        player!!.prepare()
        playerNotificationManager.setPlayer(player)
    }

    private fun createMediaItems(intent: Intent): List<MediaItem> {
        val action = intent.action
        val actionIsListView = IntentUtil.ACTION_VIEW_LIST == action
        if (!actionIsListView && IntentUtil.ACTION_VIEW != action) {
            showToast(getString(R.string.unexpected_intent_action, action))
            finish()
            return emptyList()
        }
        val mediaItems = createMediaItems(intent, getDownloadTracker( /* context= */this))

        for (i in mediaItems.indices) {
            val mediaItem = mediaItems[i]
            if (!Util.checkCleartextTrafficPermitted(mediaItem)) {
                showToast(R.string.error_cleartext_not_permitted)
                finish()
                return emptyList()
            }
            if (Util.maybeRequestReadExternalStoragePermission( /* activity= */this, mediaItem)) {
                // The player will be reinitialized if the permission is granted.
                return emptyList()
            }
            val drmConfiguration =
                Assertions.checkNotNull(mediaItem.playbackProperties).drmConfiguration
            if (drmConfiguration != null) {
                if (!FrameworkMediaDrm.isCryptoSchemeSupported(drmConfiguration.uuid)) {
                    showToast(R.string.error_drm_unsupported_scheme)
                    finish()
                    return emptyList()
                }
            }
        }
        return mediaItems
    }

    private fun releasePlayer() {
        notificationManager.cancelAll()
        playerNotificationManager.setPlayer(null)
        if (player != null) {
            updateTrackSelectorParameters()
            updateStartPosition()
            player!!.release()
            player = null
            mediaItems = emptyList()
            trackSelector = null
        }
    }

    private fun updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector!!.parameters
        }
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player!!.playWhenReady
            startWindow = player!!.currentWindowIndex
            startPosition = 0.coerceAtLeast(player!!.contentPosition.toInt()).toLong()
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private fun showToast(messageId: Int) {
        showToast(getString(messageId))
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private inner class PlayerEventListener : Player.EventListener {
        override fun onPlaybackStateChanged(@Player.State playbackState: Int) {}
        override fun onPlayerError(e: ExoPlaybackException) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition()
                initializePlayer()
            }
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray
        ) {
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                        == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        showToast(R.string.error_unsupported_video)
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                        == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        showToast(R.string.error_unsupported_audio)
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }

    private inner class PlayerErrorMessageProvider : ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString = getString(R.string.error_generic)
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    errorString = if (cause.codecInfo == null) {
                        when {
                            cause.cause is DecoderQueryException -> {
                                getString(R.string.error_querying_decoders)
                            }
                            cause.secureDecoderRequired -> {
                                getString(
                                    R.string.error_no_secure_decoder,
                                    cause.mimeType
                                )
                            }
                            else -> {
                                getString(
                                    R.string.error_no_decoder,
                                    cause.mimeType
                                )
                            }
                        }
                    } else {
                        getString(
                            R.string.error_instantiating_decoder,
                            cause.codecInfo!!.name
                        )
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }

    companion object {

        const val EXTRA_LOGO = "com.mowakib.radio.EXTRA_LOGO"
        const val EXTRA_NAME = "com.mowakib.radio.EXTRA_NAME"
        const val EXTRA_URL = "com.mowakib.radio.EXTRA_URL"
        const val EXTRA_MOST = "com.mowakib.radio.EXTRA_MOST"

        // Saved instance state keys.

        private const val CHANNEL_ID = "notif_channel"
        const val NOTIFICATION_ID = 123

        private const val KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"

        private const val TAG = "PlayerActivity"

        private var dataSourceFactory: DataSource.Factory? = null


        private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
            if (e.type != ExoPlaybackException.TYPE_SOURCE) {
                return false
            }
            var cause: Throwable? = e.sourceException
            while (cause != null) {
                if (cause is BehindLiveWindowException) {
                    return true
                }
                cause = cause.cause
            }
            return false
        }

        private fun createMediaItems(
            intent: Intent,
            downloadTracker: DownloadTracker?
        ): List<MediaItem> {
            val mediaItems: MutableList<MediaItem> = ArrayList()
            for (item in createMediaItemsFromIntent(intent)) {
                val downloadRequest =
                    downloadTracker!!.getDownloadRequest(Assertions.checkNotNull(item.playbackProperties).uri)
                if (downloadRequest != null) {
                    val builder = item.buildUpon()
                    builder
                        .setMediaId(downloadRequest.id)
                        .setUri(downloadRequest.uri)
                        .setCustomCacheKey(downloadRequest.customCacheKey)
                        .setMimeType(downloadRequest.mimeType)
                        .setStreamKeys(downloadRequest.streamKeys)
                        .setDrmKeySetId(downloadRequest.keySetId)
                        .setDrmLicenseRequestHeaders(getDrmRequestHeaders(item))
                    mediaItems.add(builder.build())
                } else {
                    mediaItems.add(item)
                }
            }
            return mediaItems
        }

        private fun getDrmRequestHeaders(item: MediaItem): Map<String, String>? {
            if (BuildConfig.DEBUG && item.playbackProperties == null) {
                error("Assertion failed")
            }
            val drmConfiguration = item.playbackProperties!!.drmConfiguration
            return drmConfiguration?.requestHeaders
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.exo_close -> finish()
        }
    }

}