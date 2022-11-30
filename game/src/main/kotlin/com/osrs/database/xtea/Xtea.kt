package com.osrs.database.xtea

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Xtea(
    val archive: Int,
    val group: Int,
    @SerialName("name_hash")
    val nameHash: Int,
    val name: String,
    @SerialName("mapsquare")
    val mapSquare: Int,
    val key: List<Int>
)
