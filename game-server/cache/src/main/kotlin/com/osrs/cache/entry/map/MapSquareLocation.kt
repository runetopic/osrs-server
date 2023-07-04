package com.osrs.cache.entry.map

@JvmInline
value class MapSquareLocation(
    val packed: Long
) {
    constructor(id: Int, x: Int, z: Int, level: Int, shape: Int, rotation: Int) : this(
        packed = ((id and 0x1FFFF) or ((shape and 0x1F) shl 17) or ((rotation and 0x3) shl 22)).toLong()
            or (((z and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)).toLong() shl 24)
    )

    inline val id: Int
        get() = (packed and 0x1FFFF).toInt()
    inline val shape: Int
        get() = (packed shr 17 and 0x1F).toInt()
    inline val rotation: Int
        get() = (packed shr 22 and 0x3).toInt()
    inline val x: Int
        get() = ((packed shr 24 and 0xFFFFFFFFL).toInt() shr 14 and 0x3FFF)
    inline val z: Int
        get() = ((packed shr 24 and 0xFFFFFFFFL).toInt() and 0x3FFF)
    inline val level: Int
        get() = ((packed shr 24 and 0xFFFFFFFFL).toInt() shr 28 and 0x3F)
}
