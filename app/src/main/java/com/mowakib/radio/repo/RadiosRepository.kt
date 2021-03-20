package com.mowakib.radio.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mowakib.radio.database.RadiosDatabase
import com.mowakib.radio.database.asRadio
import com.mowakib.radio.database.asFavorite
import com.mowakib.radio.model.Radio
import com.mowakib.radio.network.Network
import com.mowakib.radio.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RadiosRepository(private val database: RadiosDatabase) {

    val radios: LiveData<List<Radio>> =
        Transformations.map(database.radioDao.getRadios()) {
            it.asRadio()
        }

    val favRadios: LiveData<List<Radio>> =
        Transformations.map(database.radioDao.getFavRadios()) {
            it.asFavorite()
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