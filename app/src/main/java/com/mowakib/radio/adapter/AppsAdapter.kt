package com.mowakib.radio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mowakib.radio.adapter.AppsViewHolder.Companion.from
import com.mowakib.radio.databinding.AppItemBinding
import com.mowakib.radio.model.PubApp
import com.mowakib.radio.utils.ItemClick

class AppsAdapter(private val callback: ItemClick<PubApp>) :
    ListAdapter<PubApp, AppsViewHolder>(AppsCallback) {

    var apps: List<PubApp> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AppsViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        holder.binding.also {
            it.app = apps[position]
            it.callback = callback
        }
    }

}

class AppsViewHolder constructor(val binding: AppItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): AppsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AppItemBinding.inflate(layoutInflater, parent, false)
            return AppsViewHolder(binding)
        }
    }
}

object AppsCallback : DiffUtil.ItemCallback<PubApp>() {
    override fun areItemsTheSame(oldItem: PubApp, newItem: PubApp): Boolean {
        return newItem.logo == oldItem.logo
    }

    override fun areContentsTheSame(oldItem: PubApp, newItem: PubApp): Boolean {
        return newItem == oldItem
    }
}