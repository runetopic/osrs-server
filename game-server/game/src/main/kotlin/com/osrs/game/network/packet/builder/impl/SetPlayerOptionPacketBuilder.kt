package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.SetPlayerOptionPacket

@Singleton
class SetPlayerOptionPacketBuilder : PacketBuilder<SetPlayerOptionPacket>(
    opcode = 56,
    size = -1
) {
    override fun build(packet: SetPlayerOptionPacket, buffer: RSByteBuffer) {
        buffer.writeStringCp1252NullTerminated(packet.option)
        buffer.writeByteNegate(if (packet.priority) 1 else 0)
        buffer.writeByteSubtract(packet.index)
    }
}
