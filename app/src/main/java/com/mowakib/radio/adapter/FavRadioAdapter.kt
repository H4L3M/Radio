package com.mowakib.radio.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.mowakib.radio.adapter.FavRadioViewHolder.Companion.from
import com.mowakib.radio.model.Radio

class FavRadioAdapter(private val callback: RadioClick) :
    ListAdapter<Radio, FavRadioViewHolder>(DiffCallback) {

    var favRadio: List<Radio> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FavRadioViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: FavRadioViewHolder, position: Int) {

            holder.binding.also {
                it.radio = favRadio[position]
                it.radioCallback = callback
            }

    }

//    override fun onCurrentListChanged(
//        previousList: MutableList<Radio>,
//        currentList: MutableList<Radio>
//    ) {
//        super.onCurrentListChanged(previousList, currentList)
//        if (currentList != previousList) {
//            notifyDataSetChanged()
//        }
//    }
}