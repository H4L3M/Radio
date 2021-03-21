package com.mowakib.radio.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mowakib.radio.database.database
import com.mowakib.radio.repo.AppRepository
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = database(application)
    private val repository = AppRepository(database)

    init {
        viewModelScope.launch {
            repository.refreshData()
        }
    }

    val radios = repository.radios
    val favRadios = repository.favRadios

}