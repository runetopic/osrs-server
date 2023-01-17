package com.osrs.game.world.map.zone

import com.osrs.common.map.location.Location

class Zones {
    private val zones: Array<Zone?> = arrayOfNulls(2048 * 2048 * 4)

    operator fun get(location: Location): Zone = zones[location.zoneLocation.packedLocation] ?: allocateZone(location)

    fun allocateZone(location: Location): Zone {
        val createdZone = Zone(location)
        zones[location.zoneLocation.packedLocation] = createdZone
        return createdZone
    }
}
