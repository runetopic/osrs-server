package com.osrs.common.item

import com.osrs.common.map.location.Location
import com.osrs.common.controller.Controllable
import com.osrs.common.map.location.LocationSerializer
import kotlinx.serialization.Serializable

data class Item(
    val id: Int,
    val amount: Int
)

@Serializable
data class FloorItem(
    val id: Int,
    val quantity: Int,
    @Serializable(with = LocationSerializer::class)
    val location: Location,
    var timer: Int = -1
) : Controllable
