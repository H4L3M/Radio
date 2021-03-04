package com.mowakib.radio.utils

import java.text.SimpleDateFormat
import java.util.*

const val Y_M_D = "yyyyMMdd"
fun getTimestamp(): Long {
    val dfm = SimpleDateFormat(Y_M_D, Locale.getDefault())
    val unixTime = dfm.parse(getDate())?.time?.div(1000)
    return unixTime!!
}

// get date of today, yesterday, tomorrow and many other date..
fun getDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(Y_M_D, Locale.getDefault())
    calendar.add(Calendar.DATE, 0)
    return dateFormat.format(calendar.time)
}