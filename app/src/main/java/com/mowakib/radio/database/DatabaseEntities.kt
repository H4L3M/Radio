package com.mowakib.radio.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mowakib.radio.model.Radio

@Entity
data class DatabaseRadio constructor(
    @PrimaryKey
    val name: String,
    val logo: String,
    val url: String,
)

fun List<DatabaseRadio>.asDomainModel(): List<Radio> {
    return map {
        Radio(
            name = it.name,
            logo = it.logo,
            url = it.url,
        )
    }
}

@Entity
data class FavDatabaseRadio(
    @PrimaryKey
    val name: String,
    val logo: String,
    val url: String,
    val most: Long = 0,
)

fun List<FavDatabaseRadio>.asFavDomainModel(): List<Radio> {
    return map {
        Radio(
            name = it.name,
            logo = it.logo,
            url = it.url,
        )
    }
}