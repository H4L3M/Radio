package com.mowakib.radio.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mowakib.radio.database.AppDatabase
import com.mowakib.radio.database.asApp
import com.mowakib.radio.database.asFavorite
import com.mowakib.radio.database.asRadio
import com.mowakib.radio.model.PubApp
import com.mowakib.radio.model.Radio
import com.mowakib.radio.network.Network
import com.mowakib.radio.network.asDatabaseAppsModel
import com.mowakib.radio.network.asDatabaseRadioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val database: AppDatabase) {

    val radios: LiveData<List<Radio>> =
        Transformations.map(database.radioDao.getRadios()) {
            it.asRadio()
        }

    val favRadios: LiveData<List<Radio>> =
        Transformations.map(database.favRadioDao.getFavRadios()) {
            it.asFavorite()
        }

    val apps: LiveData<List<PubApp>> =
        Transformations.map(database.appDao.getPubApps()) {
            it.asApp()
        }

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            try {
                val playlist = Network.data.getRadiosAsync().await()
                database.radioDao.insertAll(*playlist.asDatabaseRadioModel())

                val listApp = Network.data.getAppsAsync().await()
                database.appDao.insertAll(*listApp.asDatabaseAppsModel())

            } catch (e: Exception) {
                Log.e(TAG, "refreshRadios: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "RadiosRepository"
    }
}