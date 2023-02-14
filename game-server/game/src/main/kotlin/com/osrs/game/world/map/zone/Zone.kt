package com.osrs.game.world.map.zone

import com.osrs.common.buffer.writeByte
import com.osrs.common.map.location.Location
import com.osrs.common.map.location.localX
import com.osrs.common.map.location.localZ
import com.osrs.common.map.location.toLocalLocation
import com.osrs.game.actor.player.Player
import com.osrs.game.item.FloorItem
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.type.server.ObjAddPacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialEnclosedPacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialFollowsPacket
import io.ktor.util.moveToByteArray
import java.nio.ByteBuffer

class Zone(
    val location: Location,
    val players: HashSet<Player> = HashSet(),
) {
    private val objs: HashSet<FloorItem> = HashSet()
    private val objRequests = HashMap<FloorItem, Boolean>()

    fun enterZone(player: Player) {
        players.add(player)
    }

    fun buildZoneUpdates(player: Player) {
        val updates = HashSet<Packet>()

        for (request in objs) {
            updates.addObj(player, request)
        }

        updates.buildZoneUpdates(player, location)
    }

    fun buildPendingZoneUpdates(player: Player) {
        val updates = HashSet<Packet>()

        for (request in objRequests) {
            val obj = request.key
            if (request.value) updates.addObj(player, obj)
        }

        updates.buildZoneUpdates(player, location)
    }

    fun requestAddObj(floorItem: FloorItem): Boolean {
        if (objRequests.containsKey(floorItem)) return false
        objRequests[floorItem] = true
        return true
    }

    fun processZoneUpdates() {
        if (objRequests.isEmpty()) return

        for (request in objRequests) {
            val obj = request.key
            if (request.value) objs.add(obj)
        }

        objRequests.clear()
    }

    private fun buildZoneUpdates(player: Player, xInScene: Int, zInScene: Int, listOfUpdates: HashSet<Packet>) {
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

    private fun HashSet<Packet>.buildZoneUpdates(player: Player, baseLocation: Location) = buildZoneUpdates(
        player,
        baseLocation.localX(player.lastLoadedLocation),
        baseLocation.localZ(player.lastLoadedLocation),
        this
    )

    private fun HashSet<Packet>.addObj(player: Player, floorItem: FloorItem) = add(
        ObjAddPacket(
            id = floorItem.id,
            amount = floorItem.amount,
            packedOffset = floorItem.location.toLocalLocation(player.lastLoadedLocation).packedOffset
        )
    )

    companion object {
        private val zonePacketIndexes = mapOf(
            ObjAddPacket::class to 10,
        )
    }
}

