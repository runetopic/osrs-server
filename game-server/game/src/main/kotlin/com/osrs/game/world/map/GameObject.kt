package com.osrs.game.world.map

import com.osrs.api.map.location.Location

@JvmInline
value class GameObject(
    private val packed: Long
) {
    constructor(id: Int, location: Location, shape: Int, rotation: Int) : this(
        packed = ((id and 0x1FFFF) or ((shape and 0x1F) shl 17) or ((rotation and 0x3) shl 22)).toLong() or (location.packed.toLong() shl 24)
    )

    val id: Int get() = (packed and 0x1FFFF).toInt()
    val shape: Int get() = (packed shr 17 and 0x1F).toInt()
    val rotation: Int get() = (packed shr 22 and 0x3).toInt()
    val location: Location get() = Location((packed shr 24 and 0xFFFFFFFFL).toInt())
}
