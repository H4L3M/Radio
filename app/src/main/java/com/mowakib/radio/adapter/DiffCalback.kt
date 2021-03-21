package com.mowakib.radio.adapter

import androidx.recyclerview.widget.DiffUtil
import com.mowakib.radio.model.Radio

object RadioCallback : DiffUtil.ItemCallback<Radio>() {
    override fun areItemsTheSame(oldItem: Radio, newItem: Radio): Boolean {
        return  newItem.id == oldItem.id
    }

    override fun areContentsTheSame(oldItem: Radio, newItem: Radio): Boolean {
        return  newItem == oldItem
    }
}



class ItemClick<T>(val block: (T) -> Unit) {
    fun onClick(t: T) = block(t)
}