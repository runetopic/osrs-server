package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeIntLittleEndian
import com.osrs.common.buffer.writeShort
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfOpenSubPacket
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
