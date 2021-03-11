package com.mowakib.radio.utils

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

//launch activity
fun Activity.launchActivity(destination: Class<*>): Intent {
    val intent = Intent(this, destination)
    startActivity(intent)
    return intent
}

//launch activity from fragment
fun Fragment.launchActivity(destination: Class<*>) = activity?.launchActivity(destination)
