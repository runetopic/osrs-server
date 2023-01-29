package com.osrs.game.network.packet.server.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeIntLittleEndian
import com.osrs.common.buffer.writeShort
import com.osrs.game.network.packet.server.IfOpenSubPacket
import com.osrs.game.network.packet.server.builder.PacketBuilder
import java.nio.ByteBuffer

@Singleton
class IfOpenSubPacketBuilder : PacketBuilder<IfOpenSubPacket>(
    opcode = 64,
    size = 7
) {
    override fun build(packet: IfOpenSubPacket, buffer: ByteBuffer) {
        buffer.writeIntLittleEndian(packet.toInterface)
        buffer.writeByteNegate(if (packet.clickThrough) 1 else 0)
        buffer.writeShort(packet.interfaceId)
    }
}
