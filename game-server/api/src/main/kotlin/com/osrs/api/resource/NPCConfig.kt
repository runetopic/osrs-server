package com.osrs.api.resource

import com.osrs.api.map.location.Location
import com.osrs.api.serializer.LocationSerializer
import kotlinx.serialization.Serializable

@Serializable
data class NPCConfig(
    val name: String? = null,
    val id: Int,
    val examine: String? = null,
    @Serializable(with = LocationSerializer::class)
    val location: Location,
    val wander: Int = 0
)
