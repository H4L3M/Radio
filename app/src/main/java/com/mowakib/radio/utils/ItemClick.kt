package com.mowakib.radio.utils

class ItemClick<T>(val block: (T) -> Unit) {
    fun onClick(t: T) = block(t)
}