package com.mowakib.radio.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mowakib.radio.R
import com.mowakib.radio.ui.bs.AdAppsFragment

//launch activity
fun Activity.launchActivity(destination: Class<*>): Intent {
    val intent = Intent(this, destination)
    startActivity(intent)
    return intent
}

//launch activity from fragment
fun Fragment.launchActivity(destination: Class<*>) = activity?.launchActivity(destination)


fun AppCompatActivity.shareApp() {
    Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra("android.intent.extra.TEXT", getString(R.string.send_message))
        startActivity(Intent.createChooser(this, getString(R.string.share_using)))
    }
}

fun AppCompatActivity.communicate() {

}

fun AppCompatActivity.moreApps() {
    AdAppsFragment.newInstance().apply {
        show(supportFragmentManager, tag)
    }
}

fun AppCompatActivity.about() {

}