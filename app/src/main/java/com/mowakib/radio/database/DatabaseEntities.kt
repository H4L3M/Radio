package com.mowakib.radio.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mowakib.radio.model.Radio

@Entity
data class DatabaseRadio constructor(
    @PrimaryKey
    val id: Int,
    val name: String,
    val logo: String,
    val flux: String,
    val isFav: Int = 0
)

fun List<DatabaseRadio>.asRadio(): List<Radio> {
    return map {
        Radio(
            id = it.id,
            name = it.name,
            logo = it.logo,
            flux = it.flux,
            isFav = it.isFav
        )
    }
}

@Entity
data class FavDatabaseRadio(
    @PrimaryKey
    val fid: Int,
    val name: String,
    val logo: String,
    val flux: String,
    val isFav: Int = 0
)

fun List<FavDatabaseRadio>.asFavorite(): List<Radio> {
    return map {
        Radio(
            id = it.fid,
            name = it.name,
            logo = it.logo,
            flux = it.flux,
            isFav = it.isFav
        )
    }
}