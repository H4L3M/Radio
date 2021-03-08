package com.mowakib.radio.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mowakib.emp.Emp
import com.mowakib.radio.R
import com.mowakib.radio.adapter.FavRadioAdapter
import com.mowakib.radio.adapter.RadioAdapter
import com.mowakib.radio.adapter.RadioClick
import com.mowakib.radio.database.FavDatabaseRadio
import com.mowakib.radio.database.RadiosDatabase
import com.mowakib.radio.database.getRadioDatabase
import com.mowakib.radio.databinding.ActivityMainBinding
import com.mowakib.radio.mediation.MediationObserver
import com.mowakib.radio.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var mainViewModel: MainViewModel

    private var radioAdapter: RadioAdapter? = null
    private var favRadioAdapter: FavRadioAdapter? = null

    private lateinit var database: RadiosDatabase
    private lateinit var emp: Emp

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var playerView: StyledPlayerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var favRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationView = binding.navView
        drawerLayout = binding.drawerLayout
        playerView = binding.appBarMain.playerView
        recyclerView = binding.appBarMain.contentMain.recyclerView
        favRecyclerView = binding.appBarMain.contentMain.favRecyclerView

        database = getRadioDatabase(applicationContext)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.appBarMain.contentMain.lifecycleOwner = this@MainActivity
        binding.appBarMain.contentMain.viewModel = mainViewModel


        lifecycle.addObserver(MediationObserver(this))

        emp = Emp.get().init(this, playerView)

        radioAdapter = getRadioAdapter()
        favRadioAdapter = getFavRadioAdapter()

        //drawer transition
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this, drawerLayout, binding.appBarMain.toolbar,
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
//        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        switchToDark()

    }

    private fun switchToDark() {
        val switch =
            navigationView.getHeaderView(0).findViewById<SwitchMaterial>(R.id.switch_to_dark)

        val isDark = getDarkMode(IS_DARK_MODE)
        if (isDark) {
            Toast.makeText(this, "$isDark", Toast.LENGTH_SHORT).show()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            Toast.makeText(this, "$isDark", Toast.LENGTH_SHORT).show()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        switch.isChecked = isDark
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "$isChecked", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveDarkMode(IS_DARK_MODE, isChecked)
            } else {
                Toast.makeText(this, "$isChecked", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveDarkMode(IS_DARK_MODE, isChecked)
            }
        }
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
                favRadios?.apply {
                    favRadioAdapter?.favRadio = favRadios
                }
            }
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = radioAdapter
        }
        favRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 5)
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

    private fun getRadioAdapter() =
        RadioAdapter(RadioClick { radio ->
            playerView.slideUp()

            playerView.findViewById<TextView>(R.id.radio_name).apply {
                text = radio.name
                isSelected = true
            }
            playerView.findViewById<AppCompatImageView>(R.id.radio_logo).apply {
                loadImage(radio.logo)
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

    private fun getFavRadioAdapter() =
        FavRadioAdapter(RadioClick { radio ->
            playerView.slideUp().fadeUp()

            playerView.findViewById<TextView>(R.id.radio_name).apply {
                text = radio.name
                isSelected = true
            }
            playerView.findViewById<AppCompatImageView>(R.id.radio_logo).apply {
                lifecycleScope.launchWhenResumed {
                    delay(100)
                    loadImage(radio.logo)
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

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> Toast.makeText(this, "0", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}