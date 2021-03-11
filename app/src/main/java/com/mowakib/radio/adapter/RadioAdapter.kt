package com.mowakib.radio.adapter

import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import com.mowakib.radio.adapter.RadioViewHolder.Companion.from
import com.mowakib.radio.model.Radio

class RadioAdapter(private val callback: RadioClick) :
    ListAdapter<Radio, RadioViewHolder>(DiffCallback) {

    var radios: List<Radio> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RadioViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.binding.also {
            it.radio = radios[position]
            it.radioCallback = callback
        }
    }
}