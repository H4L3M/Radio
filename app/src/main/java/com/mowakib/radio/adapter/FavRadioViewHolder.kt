package com.mowakib.radio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mowakib.radio.databinding.FavRadioItemBinding
import com.mowakib.radio.databinding.RadioItemBinding

class FavRadioViewHolder constructor(val binding: FavRadioItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): FavRadioViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = FavRadioItemBinding.inflate(layoutInflater, parent, false)
            return FavRadioViewHolder(binding)
        }
    }
}