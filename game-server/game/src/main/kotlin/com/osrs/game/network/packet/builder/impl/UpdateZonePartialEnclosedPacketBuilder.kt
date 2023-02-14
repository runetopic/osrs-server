package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeBytes
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateZonePartialEnclosedPacket
import java.nio.ByteBuffer

@Singleton
class UpdateZonePartialEnclosedPacketBuilder : PacketBuilder<UpdateZonePartialEnclosedPacket>(
    opcode = 105,
    size = -2
) {
    override fun build(packet: UpdateZonePartialEnclosedPacket, buffer: ByteBuffer) {
        buffer.writeByteNegate(packet.xInScene)
        buffer.writeByteNegate(packet.zInScene)
        buffer.writeBytes(packet.bytes)
    }
}
