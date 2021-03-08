package com.mowakib.radio.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mowakib.radio.R
import com.mowakib.radio.adapter.FavRadioAdapter
import com.mowakib.radio.adapter.RadioAdapter
import com.mowakib.radio.model.Radio
import com.mowakib.radio.utils.EXT
import com.mowakib.radio.utils.IMGUR_BASE_URL
import com.mowakib.radio.utils.requestBuilder

@BindingAdapter("app:listData")
fun RecyclerView.bindData(listRadio: List<Radio>?) =
    when (adapter) {
        is RadioAdapter -> (adapter as RadioAdapter).submitList(listRadio)
        is FavRadioAdapter -> (adapter as FavRadioAdapter).submitList(listRadio)
        else -> throw ExceptionInInitializerError()
    }

@BindingAdapter("app:text")
fun TextView.text(txt: String?) = txt?.apply { this@text.text = txt }

@BindingAdapter("app:goneIfNotNull")
fun View.goneIfNotNull(it: Any?) {
    this.visibility = if (it != null) View.GONE else View.VISIBLE
}

@BindingAdapter("app:goneIfNull")
fun View.goneIfNull(it: Any?) {
    this.visibility = if (it != null) View.VISIBLE else View.GONE
}

@BindingAdapter("app:show")
fun View.show(isConnected: Boolean = false) {
    this.visibility = if (isConnected) View.GONE else View.VISIBLE
}
