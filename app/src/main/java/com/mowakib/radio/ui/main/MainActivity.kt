package com.mowakib.radio.ui.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ui.StyledPlayerView
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
import com.mowakib.radio.utils.fadeDown
import com.mowakib.radio.utils.fadeUp
import com.mowakib.radio.utils.slideDown
import com.mowakib.radio.utils.slideUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var mainViewModel: MainViewModel

    private var radioAdapter: RadioAdapter? = null
    private var favRadioAdapter: FavRadioAdapter? = null

    private lateinit var database: RadiosDatabase
    private lateinit var emp: Emp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = getRadioDatabase(applicationContext)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.contentMain.lifecycleOwner = this@MainActivity
        binding.contentMain.viewModel = mainViewModel

        val playerView = binding.contentMain.playerView
        emp = Emp.get().init(this, playerView)

        radioAdapter = getRadioAdapter(playerView)
        favRadioAdapter = getFavRadioAdapter(playerView)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        //drawer transition
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this, drawerLayout, null,
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
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        drawerLayout.addDrawerListener(toggle)

    }

    override fun onStart() {
        super.onStart()

        mainViewModel.radios.observe(this, { radios ->
            radios?.apply {
                radioAdapter?.radios = radios
            }
        })
        mainViewModel.favRadios.observe(this, { favRadios ->
            if (favRadios.isNotEmpty()) {
                binding.contentMain.favoriteContainer.visibility = View.VISIBLE
                favRadios?.apply {
                    favRadioAdapter?.favRadio = favRadios
                }
            }
        })

        binding.contentMain.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = radioAdapter
        }
        binding.contentMain.favRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = favRadioAdapter
        }
    }

    private fun addRadioToFavorite(name: String, logo: String, url: String, fav: MaterialCheckBox) {
        lifecycleScope.launch(Dispatchers.IO) {
            val isFav = database.radioDao.isFav(logo)
            fav.isChecked = isFav

            val most = database.radioDao.getMost(logo)

            fav.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (fav.isChecked) {
                        database.radioDao.insert(FavDatabaseRadio(name, logo, url, 0))
                    } else {
                        database.radioDao.delete(FavDatabaseRadio(name, logo, url, most))
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.cancel()
        emp.stop()
    }

    private fun getRadioAdapter(playerView: StyledPlayerView) =
        RadioAdapter(RadioClick { radio ->
            playerView.slideUp()

            playerView.findViewById<TextView>(R.id.radio_name).apply {
                text = radio.name
                isSelected = true
            }
            playerView.findViewById<AppCompatImageView>(R.id.radio_logo).apply {
                Glide.with(this@MainActivity).load(radio.logo).into(this)
            }

            playerView.findViewById<AppCompatImageView>(R.id.close_radio).apply {
                setOnClickListener {
                    emp.apply {
                        playerView.slideDown()
                        stop()
                        destroy()
                    }
                }
            }

            val fav = playerView.findViewById<MaterialCheckBox>(R.id.radio_fav)
            addRadioToFavorite(radio.name, radio.logo, radio.url, fav)
            emp.load(radio.url)
        })

    private fun getFavRadioAdapter(playerView: StyledPlayerView) =
        FavRadioAdapter(RadioClick { radio ->
            playerView.slideUp().fadeUp()

            playerView.findViewById<TextView>(R.id.radio_name).apply {
                text = radio.name
                isSelected = true
            }
            playerView.findViewById<AppCompatImageView>(R.id.radio_logo).also {
                lifecycleScope.launchWhenResumed {
                    delay(100)
                    Glide.with(this@MainActivity).load(radio.logo).into(it)
                }
            }

            playerView.findViewById<AppCompatImageView>(R.id.close_radio).apply {
                setOnClickListener {
                    emp.apply {
                        playerView.slideDown().fadeDown()
                        stop()
                        destroy()
                    }
                }
            }

            val fav = playerView.findViewById<MaterialCheckBox>(R.id.radio_fav)
            addRadioToFavorite(radio.name, radio.logo, radio.url, fav)
            emp.load(radio.url)
        })
}