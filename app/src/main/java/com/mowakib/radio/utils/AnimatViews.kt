package com.mowakib.radio.utils

import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation

fun View.slideUp(duration: Int = 500): View {
    if (this.visibility == View.GONE || this.height == 0) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, this.height.toFloat().plus(7.0f), 0f)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }
    return this
}

fun View.slideDown(duration: Int = 500): View {
    if (this.visibility == View.VISIBLE) {
        visibility = View.GONE
        val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat().plus(7.0f))
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }
    return this
}

internal fun View.fadeUp(duration: Int = 500): View {
    if (this.visibility == View.GONE || this.height == 0) {
        visibility = View.VISIBLE
        val animate = AlphaAnimation(0F, 1F)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }
    return this
}

fun View.fadeDown(duration: Int = 500): View {
    if (this.visibility == View.VISIBLE) {
        visibility = View.GONE
        val animate = AlphaAnimation(1F, 0F)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }
    return this
}