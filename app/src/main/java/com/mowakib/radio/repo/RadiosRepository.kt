package com.mowakib.radio.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mowakib.radio.database.RadiosDatabase
import com.mowakib.radio.database.asDomainModel
import com.mowakib.radio.database.asFavDomainModel
import com.mowakib.radio.model.Radio
import com.mowakib.radio.network.Network
import com.mowakib.radio.network.asDatabaseModel

class RadiosRepository(private val database: RadiosDatabase) {

    val radios: LiveData<List<Radio>> =
        Transformations.map(database.radioDao.getRadios()) {
            it.asDomainModel()
        }

    val favRadios: LiveData<List<Radio>> =
        Transformations.map(database.radioDao.getFavRadios()) {
            it.asFavDomainModel()
        }

    suspend fun refreshRadios() {
        withContext(Dispatchers.IO) {
            try {
                val playlist = Network.radio.getRadioPlaylistAsync().await()
                database.radioDao.insertAll(*playlist.asDatabaseModel())
            } catch (e: Exception) {
                Log.e(TAG, "refreshRadios: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "RadiosRepository"
    }
}