package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeByteNegate
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.LocAddPacket
import com.osrs.game.network.packet.type.server.MapProjAnimPacket
import com.osrs.game.network.packet.type.server.ObjAddPacket
import com.osrs.game.network.packet.type.server.ObjRemovePacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialEnclosedPacket
import java.nio.ByteBuffer

@Singleton
class UpdateZonePartialEnclosedPacketBuilder : PacketBuilder<UpdateZonePartialEnclosedPacket>(
    opcode = 105,
    size = -2,
) {
    override fun build(packet: UpdateZonePartialEnclosedPacket, buffer: ByteBuffer) {
        buffer.writeByteNegate(packet.zInScene)
        buffer.writeByteNegate(packet.xInScene)
        packet.zoneUpdates?.forEach {
            val zoneUpdateBuilder = packet.builders[it::class] ?: throw IllegalStateException("Cannot write zone updates. Unhandled zone update $it")
            buffer.writeByte(zonePackets.indexOf(it::class.java))
            zoneUpdateBuilder.build(it, buffer)
        }
    }

    companion object {
        private val zonePackets = arrayOf(
            null,
            null,
            MapProjAnimPacket::class.java,
            ObjRemovePacket::class.java,
            null,
            null,
            LocAddPacket::class.java,
            null,
            null,
            null,
            ObjAddPacket::class.java,
            null,
            null,
        )
    }
}
