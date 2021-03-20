package com.mowakib.radio.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mowakib.radio.R
import com.mowakib.radio.ui.main.MainActivity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        lifecycleScope.launchWhenCreated {
            delay(0)
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.cancel()
    }

    companion object {
        private const val AUTO_HIDE_DELAY_MILLIS = 3000L
    }
}