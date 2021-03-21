package com.mowakib.radio.ui.bs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mowakib.radio.database.database
import com.mowakib.radio.repo.AppRepository

class AdAppsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = database(application.applicationContext)
    private val repo = AppRepository(database)

    val apps = repo.apps
}