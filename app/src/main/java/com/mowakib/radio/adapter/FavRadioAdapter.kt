package com.mowakib.radio.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.mowakib.radio.adapter.FavRadioViewHolder.Companion.from
import com.mowakib.radio.model.Radio

class FavRadioAdapter(private val callback: ItemClick<Radio>) :
    ListAdapter<Radio, FavRadioViewHolder>(RadioCallback) {

    var favRadio: List<Radio> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FavRadioViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: FavRadioViewHolder, position: Int) {
            holder.binding.also {
                it.radio = favRadio[position]
                it.callback = callback
            }
    }
}