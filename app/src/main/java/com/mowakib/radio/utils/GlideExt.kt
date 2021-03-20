package com.mowakib.radio.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.mowakib.radio.R
import jp.wasabeef.glide.transformations.BlurTransformation


const val IMGUR_BASE_URL = "https://i.imgur.com/"
const val EXT = ".png"

private val shimmer =
    Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
        .setDuration(1800) // how long the shimmering animation takes to do one full sweep
//        .setBaseAlpha(0.7f) //the alpha of the underlying children
//        .setHighlightAlpha(0.6f) // the shimmer alpha amount
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

// This is the placeholder for the imageView
val shimmerDrawable = ShimmerDrawable().apply {
    setShimmer(shimmer)
}

val crossFade = DrawableTransitionOptions.withCrossFade(800)

val requestBuilder = RequestOptions()
//    .placeholder(shimmerDrawable)
    .error(R.drawable.ic_glide_error)

private fun ImageView.load(path: String) =
    Glide.with(this)
        .load(path)
        .fromCache()
        .transition(crossFade)
        .apply(requestBuilder)
        .into(this)

private fun ImageView.blur(path: String) =
    Glide.with(this)
        .load(path)
        .fromCache()
        .transition(crossFade)
        .apply(requestBuilder)
        .blur()
        .into(this)

private fun <T> RequestBuilder<T>.blur(radius: Int = 25, sampling: Int = 3) =
    this.apply(RequestOptions.bitmapTransform(BlurTransformation(radius, sampling)))

private fun <T> RequestBuilder<T>.fromCache() =
    this.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).skipMemoryCache(false)

@BindingAdapter("app:glideSrc")
fun ImageView.loadImage(path: String) =
    if (path.isImgur()) load("$IMGUR_BASE_URL$path$EXT") else load(path)

fun ImageView.blurImage(path: String) =
    if (path.isImgur()) blur("$IMGUR_BASE_URL$path$EXT") else blur(path)

fun View.loadBg(path: String) =
    Glide.with(this)
        .load(path)
        .fromCache()
        .blur(20, 2)
        .into(object : CustomTarget<Drawable?>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
            this@loadBg.background = resource
        }

        override fun onLoadCleared(placeholder: Drawable?) {

        }
    })

fun View.loadBlurBg(path: String) = if (path.isImgur()) loadBg("$IMGUR_BASE_URL$path$EXT") else loadBg(path)