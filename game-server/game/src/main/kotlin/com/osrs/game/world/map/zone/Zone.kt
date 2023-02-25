package com.osrs.game.world.map.zone

import com.osrs.common.buffer.writeByte
import com.osrs.common.map.location.ZoneLocation
import com.osrs.common.map.location.distanceTo
import com.osrs.game.actor.Actor
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Player
import com.osrs.game.item.FloorItem
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.type.server.MapProjAnimPacket
import com.osrs.game.network.packet.type.server.ObjAddPacket
import com.osrs.game.network.packet.type.server.ObjRemovePacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialEnclosedPacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialFollowsPacket
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjUpdateRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ProjectileRequest
import io.ktor.util.moveToByteArray
import java.nio.ByteBuffer

class Zone(
    val location: ZoneLocation,
    val players: HashSet<Player> = HashSet(),
) {
    val objs: HashSet<FloorItem> = HashSet()

    private val projectiles = HashSet<ProjectileRequest>()

    private val zoneUpdatesRequest = HashSet<ZoneUpdateRequest>()

    fun enterZone(actor: Actor) {
        if (actor is Player) {
            players.add(actor)
        }
    }

    fun writeInitialZoneUpdates(player: Player) {
        if (objs.isEmpty() && projectiles.isEmpty()) return

        val updates = buildZoneUpdates(player)

        if (updates.isEmpty()) return

        player.writeZoneUpdates(updates)
    }

    fun writeZoneUpdates(player: Player) {
        if (!requiresUpdate()) return

        val pendingUpdates = buildPendingZoneUpdates(player)

        if (pendingUpdates.isEmpty()) return

        player.writeZoneUpdates(pendingUpdates)
    }

    private fun Player.writeZoneUpdates(updates: HashSet<Packet>) {
        val baseZoneX = baseZoneLocation.x
        val baseZoneZ = baseZoneLocation.z

        val xInScene = (this@Zone.location.x - baseZoneX) shl 3
        val zInScene = (this@Zone.location.z - baseZoneZ) shl 3

        if (updates.size == 1) {
            session.write(UpdateZonePartialFollowsPacket(xInScene, zInScene))
            session.write(updates.first())
            return
        }

        var bytes = byteArrayOf()

        for (packet in updates) {
            val zoneUpdateBuilder = session.builders[packet::class] ?: throw IllegalStateException("Cannot write zone updates. Unhandled zone update $packet")
            val buffer = ByteBuffer.allocate(1 + zoneUpdateBuilder.size)
            buffer.writeByte(zonePacketIndexes[packet::class]!!)
            zoneUpdateBuilder.build(packet, buffer)
            bytes += buffer.flip().moveToByteArray()
        }

        session.write(UpdateZonePartialEnclosedPacket(xInScene, zInScene, bytes))
    }

    private fun buildZoneUpdates(player: Player): HashSet<Packet> {
        val updates = HashSet<Packet>()

        for (request in projectiles) {
            updates.addMapProjAnim(request)
        }

        for (obj in objs) {
            updates.addObjRequest(ObjUpdateRequest(floorItem = obj), player)
        }

        return updates
    }

    private fun buildPendingZoneUpdates(player: Player): HashSet<Packet> {
        val updates = HashSet<Packet>()

        for (request in zoneUpdatesRequest) {
            when (request) {
                is ObjUpdateRequest -> {
                    updates.addObjRequest(request, player)
                }
                is ProjectileRequest -> {
                    updates.addMapProjAnim(request)
                }
            }
        }

        return updates
    }

    fun update(request: ZoneUpdateRequest) {
        zoneUpdatesRequest += request

        when (request) {
            is ObjUpdateRequest -> processObjRequest(request)
            is ProjectileRequest -> projectiles += request
            else -> throw IllegalStateException("Cannot append zone update. Unhandled zone update request $request")
        }
    }

    private fun processObjRequest(request: ObjUpdateRequest) {
        if (request.remove) {
            objs.remove(request.floorItem)
        } else {
            objs.add(request.floorItem)
        }
    }

    fun clear() {
        zoneUpdatesRequest.clear()
        projectiles.clear()
    }

    private fun HashSet<Packet>.addObjRequest(request: ObjUpdateRequest, player: Player) {
        val id = request.floorItem.id
        val quantity = request.floorItem.quantity
        val packedOffset = player.location.packedOffset

        if (request.remove) {
            this += ObjRemovePacket(id, quantity, packedOffset)
        } else {
            this += ObjAddPacket(id, quantity, packedOffset)
        }
    }

    private fun HashSet<Packet>.addMapProjAnim(request: ProjectileRequest) {
        val projectile = request.projectile

        val targetIndex = when (request.target) {
            is Player -> request.target.index + 1
            is NPC -> request.target.index + 1
            else -> 0
        }

        val from = request.from

        val to = request.target?.location ?: request.to

        val distanceX = to.x - from.x
        val distanceZ = to.z - from.z
        val flightTime = projectile.flightTime(from.distanceTo(to))

        this += MapProjAnimPacket(
            id = projectile.id,
            startHeight = projectile.startHeight,
            endHeight = projectile.endHeight,
            delay = projectile.delay,
            angle = projectile.angle,
            distOffset = projectile.distOffset,// Moves the projectile closer to the end location by units of 1/128 with 128 being a tile
            packedOffset = request.from.packedOffset,
            targetIndex = targetIndex,
            distanceX = distanceX,
            distanceZ = distanceZ,
            flightTime = flightTime,
        )
    }

    fun requiresUpdate(): Boolean = zoneUpdatesRequest.isNotEmpty() || projectiles.isNotEmpty()

    fun leaveZone(actor: Actor) {
        if (actor is Player) {
            players -= actor
        }
    }

    companion object {
        private val zonePacketIndexes = mapOf(
            MapProjAnimPacket::class to 2,
            ObjRemovePacket::class to 3,
            ObjAddPacket::class to 10,
        )
    }
}

