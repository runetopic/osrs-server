package com.osrs.common.map.location

@JvmInline
value class Location(
    val packedLocation: Int
) {
    constructor(x: Int, z: Int, level: Int = 0) : this((z and 0x3fff) or ((x and 0x3fff) shl 14) or ((level and 0x3) shl 28))

    inline val level get() = (packedLocation shr 28) and 0x3
    inline val x get() = (packedLocation shr 14) and 0x3fff
    inline val z get() = packedLocation and 0x3fff
    inline val zoneX get() = (x shr 3)
    inline val zoneZ get() = (z shr 3)
    inline val zoneId get() = zoneX or (zoneZ shl 11) or (level shl 22)
    inline val regionX get() = (x shr 6)
    inline val regionZ get() = (z shr 6)
    inline val regionId get() = (regionX shl 8) or regionZ
    inline val regionLocation get() = z shr 13 or (x shr 13 shl 8) or (level shl 16)
    inline val zoneLocation get() = ZoneLocation(zoneX, zoneZ, level)

    fun clone(): Location = Location(packedLocation)

    override fun toString(): String =
        "Location(packedCoordinates=$packedLocation, x=$x, z=$z, level=$level, zoneX=$zoneX, zoneZ=$zoneZ, zoneId=$zoneId, regionX=$regionX, regionZ=$regionZ, regionId=$regionId)"

    companion object {
        val None = Location(-1, -1, -1)
        val Default = Location(3093, 3107, 0)
    }
}

fun Location.withinDistance(other: Location, distance: Int = 15): Boolean {
    if (other.level != level) return false
    val deltaX = other.x - x
    val deltaZ = other.z - z
    return deltaX <= distance && deltaX >= -distance && deltaZ <= distance && deltaZ >= -distance
}

fun Location.localX(location: Location, size: Int = 104) = x - 8 * (location.zoneX - (size shr 4))
fun Location.localZ(location: Location, size: Int = 104) = z - 8 * (location.zoneZ - (size shr 4))

fun Location.toLocalLocation(other: Location) = LocalLocation(localX(other) shl 8 or localZ(other))

fun Location.transform(xOffset: Int, yOffset: Int, levelOffset: Int = 0) = Location(
    x = x + xOffset,
    z = z + yOffset,
    level = level + levelOffset
)
