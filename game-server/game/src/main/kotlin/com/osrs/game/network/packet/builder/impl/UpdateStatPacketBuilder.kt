package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByteSubtract
import com.osrs.common.buffer.writeIntV2
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateStatPacket
import java.nio.ByteBuffer

@Singleton
class UpdateStatPacketBuilder : PacketBuilder<UpdateStatPacket>(
    opcode = 37,
    size = 6
) {
    override fun build(packet: UpdateStatPacket, buffer: ByteBuffer) {
        buffer.writeByteSubtract(packet.level)
        buffer.writeByteSubtract(packet.id)
        buffer.writeIntV2(packet.xp.toInt())
    }
}
