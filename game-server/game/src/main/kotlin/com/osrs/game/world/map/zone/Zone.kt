package com.osrs.game.world.map.zone

import com.osrs.common.buffer.writeByte
import com.osrs.common.map.location.ZoneLocation
import com.osrs.common.map.location.distanceTo
import com.osrs.common.map.location.toLocalLocation
import com.osrs.game.actor.Actor
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Player
import com.osrs.game.command.impl.ProjectileRequest
import com.osrs.game.item.FloorItem
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.type.server.MapProjAnimPacket
import com.osrs.game.network.packet.type.server.ObjAddPacket
import com.osrs.game.network.packet.type.server.ObjRemovePacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialEnclosedPacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialFollowsPacket
import io.ktor.util.moveToByteArray
import java.nio.ByteBuffer

class Zone(
    val location: ZoneLocation,
    val players: HashSet<Player> = HashSet(),
) {
    val objs: HashSet<FloorItem> = HashSet()

    private val objRequests = HashMap<FloorItem, Boolean>()
    private val projectiles = HashSet<ProjectileRequest>()

    fun enterZone(actor: Actor) {
        if (actor is Player) {
            players.add(actor)
        }
    }

    fun writeInitialZoneUpdates(player: Player) {
        val updates = HashSet<Packet>()

        for (request in projectiles) {
            updates.addMapProjAnim(request)
        }

        for (obj in objs) {
            updates.addObj(player, obj)
        }

        updates.writeZoneUpdates(player)
    }

    fun writeZoneUpdates(player: Player) {
        if (!hasUpdate()) return

        val updates = HashSet<Packet>()

        for (request in projectiles) {
            updates.addMapProjAnim(request)
        }

        for (request in objRequests) {
            if (request.value) {
                updates.addObj(player, request.key)
            } else {
                updates.removeObj(player, request.key)
            }
        }

        updates.writeZoneUpdates(player)
    }

    fun requestAddMapProjAnim(projectile: ProjectileRequest): Boolean {
        if (projectile in projectiles) return false
        projectiles += projectile
        return true
    }

    fun requestAddObj(floorItem: FloorItem): Boolean {
        objRequests[floorItem] = true
        objs.add(floorItem)
        return true
    }

    fun requestRemoveObj(floorItem: FloorItem): Boolean {
        objRequests[floorItem] = false
        objs.remove(floorItem)
        return true
    }

    fun clear() {
        objRequests.clear()
        projectiles.clear()
    }

    private fun writeZoneUpdates(player: Player, xInScene: Int, zInScene: Int, listOfUpdates: HashSet<Packet>) {
        if (listOfUpdates.size == 1) {
            player.session.write(UpdateZonePartialFollowsPacket(xInScene, zInScene))
            player.session.write(listOfUpdates.first())
            return
        }
        var bytes = byteArrayOf()
        for (packet in listOfUpdates) {
            val packetBuilder = player.session.builders[packet::class]!!
            val buffer = ByteBuffer.allocate(1 + packetBuilder.size)
            buffer.writeByte(zonePacketIndexes[packet::class]!!)
            packetBuilder.build(packet, buffer)
            bytes += buffer.flip().moveToByteArray()
        }
        player.session.write(UpdateZonePartialEnclosedPacket(xInScene, zInScene, bytes))
    }

    private fun HashSet<Packet>.writeZoneUpdates(player: Player) {
        val baseZoneX = player.baseZoneLocation.x
        val baseZoneZ = player.baseZoneLocation.z

        val xInScene = (location.x - baseZoneX) shl 3
        val zInScene = (location.z - baseZoneZ) shl 3

        writeZoneUpdates(
            player,
            xInScene,
            zInScene,
            this
        )
    }

    private fun HashSet<Packet>.addObj(player: Player, floorItem: FloorItem) {
        add(
            ObjAddPacket(
                id = floorItem.id,
                quantity = floorItem.amount,
                packedOffset = floorItem.location.toLocalLocation(player.lastLoadedLocation).packedOffset
            )
        )
    }

    private fun HashSet<Packet>.removeObj(player: Player, floorItem: FloorItem) = add(
        ObjRemovePacket(
            id = floorItem.id,
            quantity = floorItem.amount,
            packedOffset = floorItem.location.toLocalLocation(player.lastLoadedLocation).packedOffset
        )
    )

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

        add(
            MapProjAnimPacket(
                id = projectile.id,
                startHeight = projectile.startHeight,
                endHeight = projectile.endHeight,
                delay = projectile.delay,
                angle = projectile.angle,
                distOffset = projectile.distOffset,// Moves the projectile closer to the end location by units of 1/128 with 128 being a tile
                packedOffset = ((from.x and 0x7) shl 4) or (from.z and 0x7),
                targetIndex = targetIndex,
                distanceX = distanceX,
                distanceZ = distanceZ,
                flightTime = flightTime,
            )
        )
    }

    fun hasUpdate(): Boolean = objRequests.isNotEmpty() || projectiles.isNotEmpty()

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

