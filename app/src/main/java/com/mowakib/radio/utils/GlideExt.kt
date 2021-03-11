package com.mowakib.radio.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.mowakib.radio.R
import jp.wasabeef.glide.transformations.BlurTransformation

const val IMGUR_BASE_URL = "https://i.imgur.com/"
const val EXT = ".png"

private val shimmer =
    Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
        .setDuration(1800) // how long the shimmering animation takes to do one full sweep
        .setBaseAlpha(0.7f) //the alpha of the underlying children
        .setHighlightAlpha(0.6f) // the shimmer alpha amount
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

// This is the placeholder for the imageView
val shimmerDrawable = ShimmerDrawable().apply {
    setShimmer(shimmer)
}

val requestBuilder = RequestOptions()
    .placeholder(shimmerDrawable)
    .error(R.drawable.logo_not_found)

@BindingAdapter("app:glideSrc")
fun ImageView.loadImage(path: String) =
    if (path.length < 10) {
        Glide.with(this)
            .load("$IMGUR_BASE_URL$path$EXT")
            .transition(DrawableTransitionOptions.withCrossFade(800))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(false)
            .apply(requestBuilder)
            .into(this)
    } else {
        Glide.with(this)
            .load(path)
            .transition(DrawableTransitionOptions.withCrossFade(800))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(false)
            .apply(requestBuilder)
            .into(this)
    }

fun ImageView.blurImage(path: String) =
    Glide.with(this).load(path)
        .transition(DrawableTransitionOptions.withCrossFade(800))
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(false)
        .apply(requestBuilder)
        .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
        .into(this)