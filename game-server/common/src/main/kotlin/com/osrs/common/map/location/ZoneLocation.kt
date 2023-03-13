package com.osrs.common.map.location

@JvmInline
value class ZoneLocation(
    val packedLocation: Int,
) {
    constructor(x: Int, z: Int, level: Int = 0) : this((x and 0x7ff) or ((z and 0x7ff) shl 11) or ((level and 0x3) shl 22))
    inline val id get() = x or (z shl 11) or (level shl 22)
    inline val level get() = (packedLocation shr 22) and 0x3
    inline val z get() = (packedLocation shr 11) and 0x7ff
    inline val x get() = packedLocation and 0x7ff
    inline val location get() = Location(x shl 3, z shl 3, level)
    inline val regionId get() = z + (x shl 8)
    inline val regionX get() = x shr 3
    inline val regionZ get() = z shr 3

    override fun toString(): String = "ZoneLocation($x, $z, $level)"
}

fun ZoneLocation.transform(deltaX: Int, deltaZ: Int, deltaLevel: Int = 0) = ZoneLocation(x + deltaX, z + deltaZ, level + deltaLevel)
