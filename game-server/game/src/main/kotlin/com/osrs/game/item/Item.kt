package com.osrs.game.item

import com.osrs.common.map.location.Location

data class Item(
    val id: Int,
    val amount: Int
)

data class FloorItem(
    val id: Int,
    val amount: Int,
    val location: Location
)
