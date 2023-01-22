package com.osrs.game.world.map

import com.osrs.common.map.location.Location

data class GameObject(
    val id: Int,
    val location: Location,
    val shape: Int,
    val rotation: Int
) {
    override fun toString(): String = "GameObject(id=$id, location=$location, shape=$shape, rotation=$rotation"
}
