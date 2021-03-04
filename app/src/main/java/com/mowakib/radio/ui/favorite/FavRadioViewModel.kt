package com.mowakib.radio.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mowakib.radio.database.getRadioDatabase
import com.mowakib.radio.repo.RadiosRepository

class FavRadioViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getRadioDatabase(application)
    private val favVideosRepository = RadiosRepository(database)
    val favRadios = favVideosRepository.favRadios
}