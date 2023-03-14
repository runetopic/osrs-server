package com.osrs.cache.entry.map

@JvmInline
value class MapSquareLocation(
    private val packed: Long
) {
    constructor(id: Int, x: Int, z: Int, level: Int, shape: Int, rotation: Int) : this(
        packed = ((id and 0x1FFFF) or ((shape and 0x1F) shl 17) or ((rotation and 0x3) shl 22)).toLong() or (((z and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)).toLong() shl 24)
    )

    val id: Int get() = (packed and 0x1FFFF).toInt()
    val shape: Int get() = (packed shr 17 and 0x1F).toInt()
    val rotation: Int get() = (packed shr 22 and 0x3).toInt()
    val x: Int get() = ((packed shr 24 and 0xFFFFFFFFL).toInt() shr 14 and 0x3FFF)
    val z: Int get() = ((packed shr 24 and 0xFFFFFFFFL).toInt() and 0x3FFF)
    val level: Int get() = ((packed shr 24 and 0xFFFFFFFFL).toInt() shr 28 and 0x3F)
}
