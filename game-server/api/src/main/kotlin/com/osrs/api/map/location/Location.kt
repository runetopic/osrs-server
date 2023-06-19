package com.osrs.api.map.location

import org.rsmod.pathfinder.RouteCoordinates
import kotlin.math.abs
import kotlin.math.max

@JvmInline
value class Location(
    val packed: Int
) {
    constructor(x: Int, z: Int, level: Int = 0) : this(
        packed = (z and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)
    )

    val level get() = packed shr 28 and 0x3
    val x get() = packed shr 14 and 0x3FFF
    val z get() = packed and 0x3FFF
    val zoneX get() = (x shr 3)
    val zoneZ get() = (z shr 3)
    val zoneId get() = zoneX or (zoneZ shl 11) or (level shl 22)
    val regionX get() = (x shr 6)
    val regionZ get() = (z shr 6)
    val regionId get() = (regionX shl 8) or regionZ
    val regionLocation get() = z shr 13 or (x shr 13 shl 8) or (level shl 16)
    val zoneLocation get() = ZoneLocation(zoneX, zoneZ, level)
    val packedOffset get() = ((x and 0x7) shl 4) or (z and 0x7)

    fun clone(): Location = Location(packed)

    override fun toString(): String =
        "Location(packedCoordinates=$packed, x=$x, z=$z, level=$level, zoneX=$zoneX, zoneZ=$zoneZ, zoneId=$zoneId, regionX=$regionX, regionZ=$regionZ, regionId=$regionId)"

    companion object {

        val None = Location(-1, -1, -1)
        val Default = Location(1686, 6115, 0)
    }
}

fun Location.distanceTo(to: Location): Int {
    val x = abs(to.x - x)
    val z = abs(to.z - z)
    return max(x, z)
}

fun Location.withinDistance(other: Location, distance: Int = 15): Boolean {
    if (other.level != level) return false
    val deltaX = other.x - x
    val deltaZ = other.z - z
    return deltaX <= distance && deltaX >= -distance && deltaZ <= distance && deltaZ >= -distance
}

fun Location.transform(xOffset: Int, yOffset: Int, levelOffset: Int = 0) = Location(
    x = x + xOffset,
    z = z + yOffset,
    level = level + levelOffset
)

fun Location.asRouteCoordinates(): RouteCoordinates = RouteCoordinates(x, z)

fun Location.randomize(radius: Int): Location {
    val xOffset = (-radius..radius).random()
    val zOffset = (-radius..radius).random()
<<<<<<< HEAD:game-server/api/src/main/kotlin/com/osrs/api/map/location/Location.kt
=======
    println("xOffset: $xOffset, zOffset: $zOffset")
>>>>>>> af9ba4d13b573c0ef331120b746d09ecb601c0d1:game-server/common/src/main/kotlin/com/osrs/common/map/location/Location.kt
    return Location(x + xOffset, z + zOffset, level)
}

fun randomize(minBounds: Location, maxBounds: Location): Location {
    val x = (minBounds.x..maxBounds.x).random()
    val z = (minBounds.z..maxBounds.z).random()
    return Location(x, z, minBounds.level)
}
