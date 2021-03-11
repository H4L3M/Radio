package com.mowakib.radio.adapter

import androidx.recyclerview.widget.DiffUtil
import com.mowakib.radio.model.Radio

object DiffCallback : DiffUtil.ItemCallback<Radio>() {
    override fun areItemsTheSame(oldItem: Radio, newItem: Radio): Boolean {
        return  newItem.logo == oldItem.logo
    }

    override fun areContentsTheSame(oldItem: Radio, newItem: Radio): Boolean {
        return  newItem == oldItem
    }
}
class RadioClick(val block: (Radio) -> Unit) {
    fun onClick(radio: Radio) = block(radio)
}