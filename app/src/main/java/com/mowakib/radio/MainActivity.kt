package com.mowakib.radio

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.navigation.NavigationView
import com.mowakib.emp.Emp
import com.mowakib.radio.databinding.ActivityMainBinding
import com.mowakib.radio.ui.radio.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val icons = arrayOf(
        R.drawable.ic_home,
        R.drawable.ic_favorite_filled
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val viewPager = binding.appBarMain.contentMain.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs = binding.appBarMain.tabs
        tabs.setupWithViewPager(viewPager)

        for (i in icons.indices) {
            tabs.getTabAt(i)?.icon = ContextCompat.getDrawable(this, icons[i])
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        //drawer transition
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_gallery -> {
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
    }
}