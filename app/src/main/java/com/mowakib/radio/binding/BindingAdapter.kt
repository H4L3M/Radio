package com.mowakib.radio.binding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mowakib.radio.adapter.FavRadioAdapter
import com.mowakib.radio.adapter.RadioAdapter
import com.mowakib.radio.model.Radio

@BindingAdapter("app:listData")
fun RecyclerView.bindData(listRadio: List<Radio>?) {
    when (adapter) {
        is RadioAdapter -> (adapter as RadioAdapter).submitList(listRadio)
        is FavRadioAdapter -> (adapter as FavRadioAdapter).submitList(listRadio)
        else -> throw ExceptionInInitializerError()
    }
}

@BindingAdapter("app:text")
fun TextView.text(txt: String?) = txt?.apply { this@text.text = txt }

@BindingAdapter("app:goneIfNotNull")
fun View.goneIfNotNull(it: Any?) {
    this.visibility = if (it != null) View.GONE else View.VISIBLE
}

@BindingAdapter("app:goneIfNull")
fun View.goneIfNull(it: List<Any?>?) {
    this.visibility = if (it?.size == 0) View.GONE else View.VISIBLE
}

@BindingAdapter("app:show")
fun View.show(isConnected: Boolean = false) {
    this.visibility = if (isConnected) View.GONE else View.VISIBLE
}