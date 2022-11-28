package com.osrs.service.xtea

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Xtea(
    val archive: Int,
    val group: Int,
    @SerialName("name_hash")
    val nameHash: Int,
    val name: String,
    val mapsquare: Int,
    val key: List<Int>
)
