package com.mowakib.radio.network

import com.mowakib.radio.database.DatabaseApps
import com.mowakib.radio.database.DatabaseRadio
import com.mowakib.radio.model.PubApp
import com.mowakib.radio.model.Radio

data class NetworkRadioContainer(val radios: List<Radio>)

data class NetworkAppsContainer(val apps: List<PubApp>)

fun NetworkRadioContainer.asDatabaseRadioModel(): Array<DatabaseRadio> {
    return radios.map {
        DatabaseRadio(
            id = it.id,
            name = it.name,
            logo = it.logo,
            flux = it.flux
        )
    }.toTypedArray()
}

fun NetworkAppsContainer.asDatabaseAppsModel(): Array<DatabaseApps> {
    return apps.map {
        DatabaseApps(
            name = it.name,
            logo = it.logo,
            url = it.url
        )
    }.toTypedArray()
}