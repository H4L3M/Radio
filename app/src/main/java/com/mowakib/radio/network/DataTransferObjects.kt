package com.mowakib.radio.network

import com.mowakib.radio.database.DatabaseRadio
import com.mowakib.radio.model.Radio

data class NetworkRadioContainer(val radios: List<Radio>)

fun NetworkRadioContainer.asDatabaseModel(): Array<DatabaseRadio> {
    return radios.map {
        DatabaseRadio(
            id = it.id,
            name = it.name,
            logo = it.logo,
            flux = it.flux
        )
    }.toTypedArray()
}