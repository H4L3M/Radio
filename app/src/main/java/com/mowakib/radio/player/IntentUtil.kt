/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mowakib.radio.player

import android.content.Intent
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util
import com.google.common.collect.ImmutableList
import java.util.*

/** Util to read from and populate an intent.  */
object IntentUtil {
    // Actions.
    const val ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW"
    const val ACTION_VIEW_LIST = "com.google.android.exoplayer.demo.action.VIEW_LIST"

    // Activity extras.
    const val PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders"

    // Media item configuration extras.
    private const val URI_EXTRA = "uri"
    private const val MIME_TYPE_EXTRA = "mime_type"
    private const val CLIP_START_POSITION_MS_EXTRA = "clip_start_position_ms"
    private const val CLIP_END_POSITION_MS_EXTRA = "clip_end_position_ms"
    private const val AD_TAG_URI_EXTRA = "ad_tag_uri"
    private const val DRM_SCHEME_EXTRA = "drm_scheme"
    private const val DRM_LICENSE_URI_EXTRA = "drm_license_uri"
    private const val DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties"
    private const val DRM_SESSION_FOR_CLEAR_CONTENT = "drm_session_for_clear_content"
    private const val DRM_MULTI_SESSION_EXTRA = "drm_multi_session"
    private const val DRM_FORCE_DEFAULT_LICENSE_URI_EXTRA = "drm_force_default_license_uri"
    private const val SUBTITLE_URI_EXTRA = "subtitle_uri"
    private const val SUBTITLE_MIME_TYPE_EXTRA = "subtitle_mime_type"
    private const val SUBTITLE_LANGUAGE_EXTRA = "subtitle_language"

    /** Creates a list of [media items][MediaItem] from an [Intent].  */
    @JvmStatic
    fun createMediaItemsFromIntent(intent: Intent): List<MediaItem> {
        val mediaItems: MutableList<MediaItem> = ArrayList()
        if (ACTION_VIEW_LIST == intent.action) {
            var index = 0
            while (intent.hasExtra(URI_EXTRA + "_" + index)) {
                val uri = Uri.parse(intent.getStringExtra(URI_EXTRA + "_" + index))
                mediaItems.add(
                    createMediaItemFromIntent(
                        uri, intent,  /* extrasKeySuffix= */
                        "_$index"
                    )
                )
                index++
            }
        } else {
            val uri = intent.data
            mediaItems.add(createMediaItemFromIntent(uri, intent,  /* extrasKeySuffix= */""))
        }
        return mediaItems
    }

    private fun createMediaItemFromIntent(
        uri: Uri?, intent: Intent, extrasKeySuffix: String
    ): MediaItem {
        val mimeType = intent.getStringExtra(MIME_TYPE_EXTRA + extrasKeySuffix)
        val builder = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(mimeType)
            .setAdTagUri(intent.getStringExtra(AD_TAG_URI_EXTRA + extrasKeySuffix))
            .setSubtitles(createSubtitlesFromIntent(intent, extrasKeySuffix))
            .setClipStartPositionMs(
                intent.getLongExtra(CLIP_START_POSITION_MS_EXTRA + extrasKeySuffix, 0)
            )
            .setClipEndPositionMs(
                intent.getLongExtra(
                    CLIP_END_POSITION_MS_EXTRA + extrasKeySuffix, C.TIME_END_OF_SOURCE
                )
            )
        return populateDrmPropertiesFromIntent(builder, intent, extrasKeySuffix).build()
    }

    private fun createSubtitlesFromIntent(
        intent: Intent, extrasKeySuffix: String
    ): List<MediaItem.Subtitle> {
        return if (!intent.hasExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix)) {
            emptyList()
        } else listOf(
            MediaItem.Subtitle(
                Uri.parse(intent.getStringExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix)),
                Assertions.checkNotNull(
                    intent.getStringExtra(
                        SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix
                    )
                ),
                intent.getStringExtra(SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix),
                C.SELECTION_FLAG_DEFAULT
            )
        )
    }

    private fun populateDrmPropertiesFromIntent(
        builder: MediaItem.Builder, intent: Intent, extrasKeySuffix: String
    ): MediaItem.Builder {
        val schemeKey = DRM_SCHEME_EXTRA + extrasKeySuffix
        val drmSchemeExtra = intent.getStringExtra(schemeKey) ?: return builder
        val headers: MutableMap<String, String> = HashMap()
        val keyRequestPropertiesArray = intent.getStringArrayExtra(
            DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix
        )
        if (keyRequestPropertiesArray != null) {
            var i = 0
            while (i < keyRequestPropertiesArray.size) {
                headers[keyRequestPropertiesArray[i]] = keyRequestPropertiesArray[i + 1]
                i += 2
            }
        }
        builder
            .setDrmUuid(Util.getDrmUuid(Util.castNonNull(drmSchemeExtra)))
            .setDrmLicenseUri(intent.getStringExtra(DRM_LICENSE_URI_EXTRA + extrasKeySuffix))
            .setDrmMultiSession(
                intent.getBooleanExtra(DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, false)
            )
            .setDrmForceDefaultLicenseUri(
                intent.getBooleanExtra(DRM_FORCE_DEFAULT_LICENSE_URI_EXTRA + extrasKeySuffix, false)
            )
            .setDrmLicenseRequestHeaders(headers)
        if (intent.getBooleanExtra(DRM_SESSION_FOR_CLEAR_CONTENT + extrasKeySuffix, false)) {
            builder.setDrmSessionForClearTypes(
                ImmutableList.of(
                    C.TRACK_TYPE_VIDEO,
                    C.TRACK_TYPE_AUDIO
                )
            )
        }
        return builder
    }
}