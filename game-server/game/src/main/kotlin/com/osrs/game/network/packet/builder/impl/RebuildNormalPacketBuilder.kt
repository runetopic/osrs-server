package com.osrs.game.network.packet.builder.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.database.xtea.XteaService
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.server.RebuildNormalPacket
import xlitekt.shared.buffer.writeInt
import xlitekt.shared.buffer.writeShort
import xlitekt.shared.buffer.writeShortLittleEndian
import xlitekt.shared.buffer.writeShortLittleEndianAdd
import java.nio.ByteBuffer

@Singleton
class RebuildNormalPacketBuilder @Inject constructor(
    private val xteaService: XteaService
) : PacketBuilder<RebuildNormalPacket>(
    opcode = 103,
    size = -2
) {
    override fun build(packet: RebuildNormalPacket, buffer: ByteBuffer) {
        if (packet.initialize) packet.viewport.init(buffer)

        val zoneX = packet.location.zoneX
        val zoneZ = packet.location.zoneZ

        buffer.writeShortLittleEndian(zoneX)
        buffer.writeShortLittleEndianAdd(zoneZ)

        val zonesX = ((zoneX - 6) / 8..(zoneX + 6) / 8)
        val zonesZ = ((zoneZ - 6) / 8..(zoneZ + 6) / 8)

        buffer.writeShort(zonesX.count() * zonesZ.count())

        for (x in zonesX) {
            for (z in zonesZ) {
                val regionId = z + (x shl 8)
                val xteaKeys = xteaService.find(regionId)
                for (key in xteaKeys) {
                    buffer.writeInt(key)
                }
            }
        }
    }
}
