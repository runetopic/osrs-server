package com.osrs.game.world.map.zone

import com.osrs.common.map.location.Location
import com.osrs.common.map.location.ZoneLocation

class ZoneManager {
    val zones: Array<Zone?> = arrayOfNulls(2048 * 2048 * 4)

    operator fun get(location: Location): Zone {
        val zoneLocation = location.zoneLocation
        return zones[zoneLocation.packedLocation] ?: createZone(zoneLocation)
    }

    operator fun get(zoneLocation: ZoneLocation): Zone {
        return zones[zoneLocation.packedLocation] ?: createZone(zoneLocation)
    }

    fun createZone(location: ZoneLocation): Zone {
        val currentZone = zones[location.packedLocation]
        if (currentZone != null) return currentZone
        val newZone = Zone(location.location)
        zones[location.packedLocation] = newZone
        return newZone
    }
}
