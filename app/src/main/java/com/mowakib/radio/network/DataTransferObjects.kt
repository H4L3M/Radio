package com.mowakib.radio.network

import com.mowakib.radio.database.DatabaseRadio
import com.mowakib.radio.model.Radio


/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
//@JsonClass(generateAdapter = true)
data class NetworkRadioContainer(val radios: List<Radio>)

///**
// * Videos represent a devbyte that can be played.
// */
////@JsonClass(generateAdapter = true)
//data class NetworkRadio(
//    val name: String,
//    val logo: String,
//    val url: String
//)
//
///**
// * Convert Network results to database objects
// */
//fun NetworkRadioContainer.asDomainModel(): List<Radio> {
//    return radios.map {
//        Radio(
//            name = it.name,
//            logo = it.logo,
//            url = it.url
//        )
//    }
//}

fun NetworkRadioContainer.asDatabaseModel(): Array<DatabaseRadio> {
    return radios.map {
        DatabaseRadio(
            name = it.name,
            logo = it.logo,
            url = it.url,
        )
    }.toTypedArray()
}