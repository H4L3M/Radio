package com.mowakib.radio.utils

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.mowakib.radio.R

fun Context.isDark(): Boolean =
    PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(this.getString(R.string.is_dark), false)