package com.mowakib.radio.utils

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mowakib.radio.R

const val IMGUR_BASE_URL = "https://i.imgur.com/"
const val EXT = ".png"

val requestBuilder = RequestOptions()
    .placeholder(R.drawable.loadin_animation)
    .error(R.drawable.logo_not_found)

@BindingAdapter("app:glideSrc")
fun ImageView.loadImage(path: String) =
    if (path.length < 10) {
        Glide.with(this)
            .load("$IMGUR_BASE_URL$path$EXT")
            .apply(requestBuilder)
            .into(this)
    } else {
        Glide.with(this)
            .load(path)
            .apply(requestBuilder)
            .into(this)
    }

