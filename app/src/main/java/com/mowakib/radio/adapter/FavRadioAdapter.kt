package com.mowakib.radio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mowakib.radio.adapter.FavRadioAdapter.FavRadioViewHolder.Companion.from
import com.mowakib.radio.databinding.FavRadioItemBinding
import com.mowakib.radio.model.Radio
import com.mowakib.radio.utils.ItemClick

class FavRadioAdapter(private val callback: ItemClick<Radio>) :
    ListAdapter<Radio, FavRadioAdapter.FavRadioViewHolder>(RadioCallback) {

    var favRadio: List<Radio> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): FavRadioViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: FavRadioViewHolder, position: Int) {
        holder.binding.also {
            it.radio = favRadio[position]
            it.callback = callback
        }
    }

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
}