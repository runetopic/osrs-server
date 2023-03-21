package com.osrs.game.network.packet.builder.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.common.map.MapSquares
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.RebuildNormalPacket

@Singleton
class RebuildNormalPacketBuilder @Inject constructor(
    private val mapSquares: MapSquares
) : PacketBuilder<RebuildNormalPacket>(
    opcode = 0,
    size = -2
) {
    override fun build(packet: RebuildNormalPacket, buffer: RSByteBuffer) {
        if (packet.initialize) packet.viewport.init(buffer)

        val zoneX = packet.location.zoneX
        val zoneZ = packet.location.zoneZ

        buffer.writeShortLittleEndian(zoneZ)
        buffer.writeShortLittleEndianAdd(zoneX)

        val zonesX = ((zoneX - 6) / 8..(zoneX + 6) / 8)
        val zonesZ = ((zoneZ - 6) / 8..(zoneZ + 6) / 8)

        buffer.writeShort(zonesX.count() * zonesZ.count())

        for (x in zonesX) {
            for (z in zonesZ) {
                val id = z + (x shl 8)
                val xteaKeys = mapSquares[id]?.key ?: throw IllegalStateException("Missing data for map square with id: $id")
                for (key in xteaKeys) {
                    buffer.writeInt(key)
                }
            }
        }
    }
}
