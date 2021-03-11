package com.mowakib.radio.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mowakib.radio.database.getRadioDatabase
import com.mowakib.radio.repo.RadiosRepository
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getRadioDatabase(application)
    private val videosRepository = RadiosRepository(database)

    init {
        viewModelScope.launch {
            videosRepository.refreshRadios()
        }
    }

    val radios = videosRepository.radios
    val favRadios = videosRepository.favRadios

}