package com.osrs.api.map.location

@JvmInline
value class ZoneLocation(
    val packedLocation: Int
) {
    constructor(x: Int, z: Int, level: Int = 0) : this((x and 0x7ff) or ((z and 0x7ff) shl 11) or ((level and 0x3) shl 22))
    val id get() = x or (z shl 11) or (level shl 22)
    val level get() = (packedLocation shr 22) and 0x3
    val z get() = (packedLocation shr 11) and 0x7ff
    val x get() = packedLocation and 0x7ff
    val location get() = Location(x shl 3, z shl 3, level)
    val regionId get() = z + (x shl 8)
    val regionX get() = x shr 3
    val regionZ get() = z shr 3

    override fun toString(): String = "ZoneLocation($x, $z, $level)"
}

fun ZoneLocation.transform(deltaX: Int, deltaZ: Int, deltaLevel: Int = 0) = ZoneLocation(x + deltaX, z + deltaZ, level + deltaLevel)
