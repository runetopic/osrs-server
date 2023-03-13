package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeByteSubtract
import com.osrs.common.buffer.writeStringCp1252NullTerminated
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.SetPlayerOptionPacket
import java.nio.ByteBuffer

@Singleton
class SetPlayerOptionPacketBuilder : PacketBuilder<SetPlayerOptionPacket>(
    opcode = 56,
    size = -1,
) {
    override fun build(packet: SetPlayerOptionPacket, buffer: ByteBuffer) {
        buffer.writeStringCp1252NullTerminated(packet.option)
        buffer.writeByteNegate(if (packet.priority) 1 else 0)
        buffer.writeByteSubtract(packet.index)
    }
}
