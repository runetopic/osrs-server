package com.osrs.game.item

import com.osrs.common.map.location.Location
import com.osrs.game.controller.Controllable

data class Item(
    val id: Int,
    val amount: Int
)

data class FloorItem(
    val id: Int,
    val quantity: Int,
    val location: Location,
    var timer: Int = -1
) : Controllable
