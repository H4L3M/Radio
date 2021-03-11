package com.mowakib.radio.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.navigation.NavigationView
import com.mowakib.emp.Emp
import com.mowakib.radio.R
import com.mowakib.radio.adapter.FavRadioAdapter
import com.mowakib.radio.adapter.RadioAdapter
import com.mowakib.radio.adapter.RadioClick
import com.mowakib.radio.database.FavDatabaseRadio
import com.mowakib.radio.database.RadiosDatabase
import com.mowakib.radio.database.getRadioDatabase
import com.mowakib.radio.databinding.ActivityMainBinding
import com.mowakib.radio.model.Radio
import com.mowakib.radio.utils.blurImage
import com.mowakib.radio.utils.loadImage
import com.mowakib.radio.utils.slideDown
import com.mowakib.radio.utils.slideUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private var radioAdapter: RadioAdapter? = null
    private var favRadioAdapter: FavRadioAdapter? = null

    private lateinit var database: RadiosDatabase
    private lateinit var emp: Emp

    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var playerContainer: MaterialCardView
    private lateinit var recyclerView: RecyclerView
    private lateinit var favRecyclerView: RecyclerView
    private lateinit var playerView: StyledPlayerView
    private lateinit var radioLogoBig: ImageFilterView

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbarLayout: CollapsingToolbarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            appBarMain.contentMain.viewModel = mainViewModel
            appBarMain.contentMain.lifecycleOwner = this@MainActivity
//        lifecycle.addObserver(MediationObserver(this))

            this@MainActivity.navView = navView
            this@MainActivity.drawerLayout = drawerLayout

            playerView = appBarMain.playerView
            appBarLayout = appBarMain.appBar
            radioLogoBig = appBarMain.radioLogoBig
            toolbarLayout = appBarMain.toolbarLayout
            playerContainer = appBarMain.playerContainer
            recyclerView = appBarMain.contentMain.recyclerView
            favRecyclerView = appBarMain.contentMain.favRecyclerView
            setContentView(root)
        }

        database = getRadioDatabase(applicationContext)

        emp = Emp.get().init(this, playerView)

        radioAdapter = getRadioAdapter()
        favRadioAdapter = getFavRadioAdapter()

        //drawer transition
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarMain.toolbar,
            R.string.app_name,
            R.string.app_name
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val slideX = drawerView.width * slideOffset
//                if (isArabic()) {
//                    binding.holder.translationZ = -slideX
//                } else {
                binding.holder.translationX = slideX
//                }
//                binding.holder.scaleX = 1 - slideOffset / 20f
                binding.holder.scaleY = 1 - slideOffset / 20f
                super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        radioLogoBig.loadImage(BG)


        toolbarLayout.setOnDragListener { v, event ->
            Toast.makeText(this, "${event.result}", Toast.LENGTH_SHORT).show()
            return@setOnDragListener true
        }

    }

    public override fun onStart() {
        super.onStart()
        bind()
    }

    override fun onPause() {
        super.onPause()
        emp.pause()
        playerContainer.slideDown()
    }

    override fun onStop() {
        super.onStop()
        emp.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        emp.destroy()
        lifecycleScope.cancel()
    }

    private fun bind() {

        mainViewModel.radios.observe(this, Observer { radios ->
            radios.apply {
                radioAdapter?.radios = radios
            }
        })

        mainViewModel.favRadios.observe(this, Observer { favRadios ->
            Toast.makeText(this, "${favRadios.size}", Toast.LENGTH_SHORT).show()
            favRadios.apply {
                favRadioAdapter?.favRadio = favRadios
            }
        })

        recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = radioAdapter
        }
        favRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = favRadioAdapter
        }
    }

    private fun addRadioToFavorite(radio: FavDatabaseRadio, fav: MaterialCheckBox) {
        lifecycleScope.launch(Dispatchers.IO) {
            val isFav = database.radioDao.isFav(radio.logo)
            fav.isChecked = isFav

            fav.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (fav.isChecked) {
                        database.radioDao.insert(radio)
                    } else {
                        database.radioDao.delete(radio)
                    }
                }
            }
        }
    }

    private fun getRadioAdapter() =
        RadioAdapter(RadioClick { radio -> playAndStoreToFav(radio) })

    private fun getFavRadioAdapter() =
        FavRadioAdapter(RadioClick { radio -> playAndStoreToFav(radio) })

    private fun playAndStoreToFav(radio: Radio) {
        radioLogoBig.blurImage(radio.logo)
        toolbarLayout.title = radio.name

        playerContainer.slideUp()

        playerView.findViewById<TextView>(R.id.radio_name).apply {
            text = radio.name
            isSelected = true
        }
        playerView.findViewById<AppCompatImageView>(R.id.radio_logo).loadImage(radio.logo)


        playerView.findViewById<AppCompatImageView>(R.id.close_radio).apply {
            setOnClickListener {
                emp.apply {
                    playerContainer.slideDown()
                    stop()
                    destroy()
                }
                radioLogoBig.loadImage(BG)
                toolbarLayout.title = resources.getString(R.string.app_name)

            }
        }

        val fav = playerView.findViewById<MaterialCheckBox>(R.id.radio_fav)
        addRadioToFavorite(FavDatabaseRadio(radio.name, radio.logo, radio.url), fav)
        emp.load(radio.url)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return true
    }

    companion object {
        private const val BG =
            "https://i.pinimg.com/originals/a9/44/e1/a944e15bf25e4a4d167b8f6b845913c8.jpg"
    }
}