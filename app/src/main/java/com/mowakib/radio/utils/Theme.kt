package com.mowakib.radio.utils

import androidx.appcompat.app.AppCompatDelegate

fun switchTheme(isDark: Boolean) {
    if (isDark) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}