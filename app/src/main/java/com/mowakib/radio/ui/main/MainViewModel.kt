package com.mowakib.radio.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.mowakib.radio.database.getRadioDatabase
import com.mowakib.radio.mediation.MediationObserver
import com.mowakib.radio.repo.RadiosRepository

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