package com.osrs.network.packet.builder

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.network.packet.RebuildNormalPacket
import com.osrs.service.xtea.XteaService
import xlitekt.shared.buffer.writeInt
import xlitekt.shared.buffer.writeShort
import xlitekt.shared.buffer.writeShortAdd
import xlitekt.shared.buffer.writeShortLittleEndian
import java.nio.ByteBuffer

@Singleton
class RebuildNormalPacketBuilder @Inject constructor(
    private val xteaService: XteaService
) : PacketBuilder<RebuildNormalPacket>(
    opcode = 16,
    size = -2
) {
    override fun build(packet: RebuildNormalPacket, writePool: ByteBuffer) {
        if (packet.initialize) {
            packet.viewport.init(writePool, packet.players)
        }

        val zoneX = packet.location.zoneX
        val zoneZ = packet.location.zoneZ

        writePool.writeShortAdd(zoneZ)
        writePool.writeShortLittleEndian(zoneX)
        val zonesX = ((zoneX - 6) / 8..(zoneX + 6) / 8)
        val zonesZ = ((zoneZ - 6) / 8..(zoneZ + 6) / 8)

        writePool.writeShort(zonesX.count() * zonesZ.count())

        for (x in zonesX) {
            for (z in zonesZ) {
                val regionId = z + (x shl 8)
                val xteaKeys = xteaService.find(regionId)
                for (key in xteaKeys) {
                    writePool.writeInt(key)
                }
            }
        }
    }
}
