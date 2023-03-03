package com.osrs.game.world.map.zone

import com.osrs.common.map.location.ZoneLocation
import com.osrs.common.map.location.distanceTo
import com.osrs.game.actor.Actor
import com.osrs.game.actor.player.Player
import com.osrs.game.item.FloorItem
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.type.server.MapProjAnimPacket
import com.osrs.game.network.packet.type.server.ObjAddPacket
import com.osrs.game.network.packet.type.server.ObjRemovePacket
import com.osrs.game.network.packet.type.server.UpdateZoneFullFollowsPacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialEnclosedPacket
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjAddRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjRemoveRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjUpdateRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ProjectileRequest

class Zone(
    val location: ZoneLocation,
    val players: HashSet<Player> = HashSet()
) {
    val objs: HashSet<FloorItem> = HashSet()

    private val zoneUpdateRequest = ArrayList<ZoneUpdateRequest>()
    private val zoneUpdates = ArrayList<Packet>()

    fun enterZone(actor: Actor) {
        if (actor is Player) {
            players.add(actor)
        }
    }

    fun leaveZone(actor: Actor) {
        if (actor is Player) {
            players -= actor
        }
    }

    fun writeInitialZoneUpdates(player: Player) {
        if (objs.isNotEmpty()) {
            for (obj in objs) {
                val id = obj.id
                val quantity = obj.quantity
                val packedOffset = obj.location.packedOffset
                player.write(ObjAddPacket(id, quantity, packedOffset))
            }
        }
    }

    fun writeZoneUpdates(player: Player) {
        val baseZoneX = player.baseZoneLocation.x
        val baseZoneZ = player.baseZoneLocation.z

        val xInScene = (location.x - baseZoneX) shl 3
        val zInScene = (location.z - baseZoneZ) shl 3

        if (!requiresUpdate()) return

        player.session.write(UpdateZoneFullFollowsPacket(xInScene, zInScene))

        for (obj in objs) {
            val id = obj.id
            val quantity = obj.quantity
            val packedOffset = obj.location.packedOffset
            player.write(ObjAddPacket(id, quantity, packedOffset))
        }

        val sharedUpdates = ZoneManager.getZoneUpdates(location.packedLocation)

        if (sharedUpdates != null) {
            player.session.write(
                UpdateZonePartialEnclosedPacket(
                    xInScene,
                    zInScene,
                    sharedUpdates,
                    player.session.builders
                )
            )
        }
    }

    fun update(request: ZoneUpdateRequest) {
        zoneUpdateRequest += request

        if (request is ObjAddRequest) addObj(request)
        if (request is ObjRemoveRequest) removeObj(request)
        if (request is ObjUpdateRequest) TODO()
    }

    private fun addObj(request: ObjAddRequest) {
        objs.add(request.floorItem)
    }

    private fun removeObj(request: ObjRemoveRequest) {
        objs.remove(request.floorItem)
    }

    fun buildSharedUpdates(sharedUpdates: Sequence<ZoneUpdateRequest>): Sequence<Packet> = sequence {
        for (request in sharedUpdates) {
            if (request is ObjRemoveRequest) {
                yieldObjRemovePacket(request)
            }
            if (request is ProjectileRequest) {
                yieldMapAnimProjPacket(request)
            }
        }
    }
    private suspend fun SequenceScope<Packet>.yieldObjRemovePacket(request: ObjRemoveRequest) {
        val floorItem = request.floorItem
        val packedOffset = floorItem.location.packedOffset
        yield(ObjRemovePacket(floorItem.id, floorItem.quantity, packedOffset))
    }

    private suspend fun SequenceScope<Packet>.yieldMapAnimProjPacket(request: ProjectileRequest) {
        val targetIndex = (request.target?.index ?: 0) + 1

        val from = request.from

        val to = request.target?.location ?: request.to

        val distanceX = to.x - from.x
        val distanceZ = to.z - from.z
        val flightTime = request.projectile.flightTime(from.distanceTo(to))

        yield(
            MapProjAnimPacket(
                id = request.projectile.id,
                startHeight = request.projectile.startHeight,
                endHeight = request.projectile.endHeight,
                delay = request.projectile.delay,
                angle = request.projectile.angle,
                distOffset = request.projectile.distOffset,// Moves the projectile closer to the end location by units of 1/128 with 128 being a tile
                packedOffset = request.from.packedOffset,
                targetIndex = targetIndex,
                distanceX = distanceX,
                distanceZ = distanceZ,
                flightTime = flightTime,
            )
        )
    }

    fun clear() {
        zoneUpdates.clear()
        zoneUpdateRequest.clear()
    }

    fun getZoneUpdateRequests() : List<ZoneUpdateRequest> = zoneUpdateRequest

    fun requiresUpdate(): Boolean = zoneUpdateRequest.isNotEmpty()
}

