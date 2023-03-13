package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByteAdd
import com.osrs.common.buffer.writeIntV2
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfOpenSubPacket
import java.nio.ByteBuffer

@Singleton
class IfOpenSubPacketBuilder : PacketBuilder<IfOpenSubPacket>(
    opcode = 89,
    size = 7,
) {
    override fun build(packet: IfOpenSubPacket, buffer: ByteBuffer) {
        buffer.writeShortLittleEndian(packet.interfaceId)
        buffer.writeByteAdd(if (!packet.isModal) 1 else 0)
        buffer.writeIntV2(packet.toInterface)
    }
}
