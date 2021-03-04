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

@BindingAdapter("app:listRadio")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Radio>?) {
    val adapter = recyclerView.adapter as RadioAdapter
    adapter.submitList(data)
}

@BindingAdapter("app:listFavRadio")
fun bindFavRecyclerView(recyclerView: RecyclerView, data: List<Radio>?) {
    val adapter = recyclerView.adapter as FavRadioAdapter
    adapter.submitList(data)
}

@BindingAdapter("app:src")
fun bindImageView(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        if (imgUrl.length < 10) {
            Glide.with(imageView.context).load("https://i.imgur.com/$imgUrl.png")
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loadin_animation)
                        .error(R.drawable.logo_not_found)).into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(imgUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loadin_animation)
                        .error(R.drawable.logo_not_found)).into(imageView)
        }
    }
}

@BindingAdapter("app:text")
fun bindTextView(tvTextName: TextView, textName: String?) {
    textName?.let {
        tvTextName.text = it
    }
}

@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}