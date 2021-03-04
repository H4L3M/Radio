package com.mowakib.radio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mowakib.radio.databinding.RadioItemBinding

class RadioViewHolder constructor(val binding: RadioItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): RadioViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RadioItemBinding.inflate(layoutInflater, parent, false)
            return RadioViewHolder(binding)
        }
    }
}