package com.osrs.game.world.map.zone

import com.osrs.common.map.location.ZoneLocation
import com.osrs.game.network.packet.Packet
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjAddRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjRemoveRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjUpdateRequest

object ZoneManager {
    val zones: Array<Zone?> = arrayOfNulls(2048 * 2048 * 4)

    private val observedZones = mutableSetOf<Int>()
    private val globalUpdates = mutableMapOf<Int, Sequence<Packet>>()

    operator fun get(zoneLocation: ZoneLocation): Zone = zones[zoneLocation.packedLocation] ?: createZone(zoneLocation)

    private fun createZone(location: ZoneLocation): Zone = Zone(location).also {
        zones[location.packedLocation] = it
    }

    fun appendObservedZone(zones: IntArray) {
        for (zone in zones) {
            observedZones += zone
        }
    }

    fun buildSharedZoneUpdates() {
        for (trackedZone in observedZones) {
            val zone = get(ZoneLocation(trackedZone))
            val updates = zone.getZoneUpdateRequests()
            if (updates.isEmpty()) continue
            val sharedUpdates = updates.asSequence().filter(::filterSharedUpdates)
            if (sharedUpdates.none()) continue
            globalUpdates[trackedZone] = zone.buildSharedUpdates(sharedUpdates)
        }
    }

    private fun filterSharedUpdates(request: ZoneUpdateRequest): Boolean {
        if (request is ObjAddRequest || request is ObjUpdateRequest) return false
        if (request is ObjRemoveRequest && request.receiver != -1) return false
        return true
    }

    fun clear() {
        if (observedZones.isNotEmpty()) observedZones.clear()
        if (globalUpdates.isNotEmpty()) globalUpdates.clear()
    }

    fun getGlobalZoneUpdates(zoneLocation: Int): Sequence<Packet>? = globalUpdates[zoneLocation]
}
