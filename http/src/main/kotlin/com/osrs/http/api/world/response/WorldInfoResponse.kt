package com.osrs.http.api.world.response

import kotlinx.serialization.Serializable

@Serializable
data class WorldInfoData(
    val players: Int
)

@Serializable
data class WorldInfoResponse(
    val message: String,
    val data: WorldInfoData? = null
)
