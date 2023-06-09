package com.osrs.game.actor

import com.osrs.api.map.location.Location
import com.osrs.api.map.location.ZoneLocation
import com.osrs.game.actor.movement.Direction
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.actor.movement.MovementQueue
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.ActorRenderer
import com.osrs.game.network.packet.type.server.UpdateZoneFullFollowsPacket
import com.osrs.game.world.World
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class Actor(
    val world: World
) {
    // Immutable properties.
    val renderer = ActorRenderer()
    val movementQueue: MovementQueue = MovementQueue()
    val zones = IntArray(7 * 7)

    // Mutable properties.
    var online = false
    var index = 0
    var isRunning = false
        private set
    var runEnergy = 10_000f
        private set
    var moveDirection = MoveDirection.None
    var location = Location.None
        private set
    var lastLocation = Location.None
        private set
    var zone = world.zone(location)
        private set
    var lastLoadedLocation = Location.None
        private set
    var baseZoneLocation = Location.None.zoneLocation
        private set

    abstract fun login()
    abstract fun logout()
    abstract fun totalHitpoints(): Int
    abstract fun currentHitpoints(): Int
    abstract fun processMovement()

    fun initialize(location: Location) {
        world.collisionMap.addActorCollision(location)
        this.location = location
        this.lastLocation = location
    }

    open fun updateMap(initialize: Boolean) {
        lastLoadedLocation = location.clone()
        baseZoneLocation = ZoneLocation(x = location.zoneX - 6, z = location.zoneZ - 6)
    }

    fun process() {
        processMovement()

        if (shouldRebuildMap()) {
            updateMap(false)
        }

        if (shouldUpdateZones()) {
            updateZones()
        }
    }

    fun reset(resetRenderer: Boolean) {
        if (resetRenderer) {
            renderer.clearUpdates()
        }
        if (moveDirection != MoveDirection.None) {
            moveDirection = MoveDirection.None
        }
        if (lastLocation != location) {
            lastLocation = location
        }
    }
    fun canTravel(location: Location, direction: Direction): Boolean = world.collisionMap.canTravel(location, direction, this is NPC)

    fun moveTo(newLocation: Location, moveDirection: MoveDirection) {
        world.collisionMap.removeActorCollision(location)
        this.location = newLocation
        world.collisionMap.addActorCollision(newLocation)
        this.moveDirection = moveDirection
    }

    private fun shouldRebuildMap(buildArea: Int = 104): Boolean {
        if (lastLoadedLocation == location) return false

        val lastZoneX = lastLoadedLocation.zoneX
        val lastZoneZ = lastLoadedLocation.zoneZ
        val zoneX = location.zoneX
        val zoneZ = location.zoneZ
        val limit = ((buildArea shr 3) / 2) - 1
        return abs(lastZoneX - zoneX) >= limit || abs(lastZoneZ - zoneZ) >= limit
    }

    private fun updateZones() {
        zone.leaveZone(this)
        zone = world.zone(location)
        zone.enterZone(this)

        val baseZoneX = baseZoneLocation.x
        val baseZoneZ = baseZoneLocation.z
        // Zones on the x-axis.
        val rangeX = max(baseZoneX, location.zoneX - 3)..min(baseZoneX + 11, location.zoneX + 3)
        // Zones on the z-axis.
        val rangeZ = max(baseZoneZ, location.zoneZ - 3)..min(baseZoneZ + 11, location.zoneZ + 3)
        // Clone down the current zones used to check if we need to send updates to a new zone being added to this player.
        val existing = zones.clone()
        // Clear out our current zones.
        zones.fill(0)
        // Then refill our zones and send updates to the client for new zones.
        for (x in rangeX) {
            for (z in rangeZ) {
                // The next available index in our zones.
                val index = zones.indexOf(0)
                if (index == -1) throw AssertionError("Zones does not have an available slot for zone at $x, $z which should not happen.")
                val zoneLocation = ZoneLocation(x, z, location.level)
                val zonePackedLocation = zoneLocation.packedLocation

                zones[index] = zonePackedLocation

                if (this !is Player) continue

                if (zonePackedLocation !in existing) {
                    val xInScene = (zoneLocation.x - baseZoneX) shl 3
                    val yInScene = (zoneLocation.z - baseZoneZ) shl 3
                    session.write(UpdateZoneFullFollowsPacket(xInScene, yInScene))
                    world.zone(zoneLocation).writeInitialZoneUpdates(this)
                }
            }
        }
    }

    private fun shouldUpdateZones() = zone.location.id != location.zoneId
}
