package com.mowakib.radio.utils

import android.content.Context

private const val DARK_MODE_PREF = "com.mowakib.radio.DARK_MODE_PREF"
const val IS_DARK_MODE = "isDark"

fun Context.saveDarkMode(key: String, value: Boolean) =
    getSharedPreferences(DARK_MODE_PREF, Context.MODE_PRIVATE).edit().apply {
        putBoolean(key, value)
    }.apply()

fun Context.getDarkMode(name: String): Boolean =
    getSharedPreferences(DARK_MODE_PREF, Context.MODE_PRIVATE).getBoolean(name, false)