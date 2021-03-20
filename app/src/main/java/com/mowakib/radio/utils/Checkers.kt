package com.mowakib.radio.utils


fun String?.isImgur(): Boolean {
    return this != null && this.length < 10
}