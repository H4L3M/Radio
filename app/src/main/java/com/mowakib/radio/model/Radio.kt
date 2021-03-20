package com.mowakib.radio.model

data class Radio(
    val id: Int,
    val logo: String,
    val name: String,
    val flux: String,
    val isFav: Int = 0
)