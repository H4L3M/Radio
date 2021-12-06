package com.mowakib.radio.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mowakib.radio.R
import com.mowakib.radio.ui.bs.AdAppsFragment

//launch activity
fun Activity.startActivity(destination: Class<*>): Intent {
    val intent = Intent(this, destination)
    startActivity(intent)
    return intent
}

//launch activity from fragment
fun Fragment.startActivity(destination: Class<Activity>) =
    activity?.startActivity(destination)

fun AppCompatActivity.shareApp() =
    chooserIntent(TYPE_TEXT, getString(R.string.send_message))

fun AppCompatActivity.communicate() =
    chooserIntent(TYPE_EMAIL, getString(R.string.mail))

fun AppCompatActivity.moreApps() {
    AdAppsFragment.newInstance().apply {
        showNow(supportFragmentManager, tag)
    }
}

private fun Activity.chooserIntent(mimeType: String, extra: String) {
    Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_EMAIL, extra)
        startActivity(Intent.createChooser(this, getString(R.string.share_using)))
    }
}