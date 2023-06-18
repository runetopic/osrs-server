package com.osrs.api.item

import com.osrs.api.controller.Controllable
import com.osrs.api.map.location.Location
import com.osrs.api.serializer.LocationSerializer
import kotlinx.serialization.Serializable

@Serializable
data class FloorItem(
    val id: Int,
    val quantity: Int,
    @Serializable(with = LocationSerializer::class)
    val location: Location,
    var timer: Int = -1
) : Controllable
