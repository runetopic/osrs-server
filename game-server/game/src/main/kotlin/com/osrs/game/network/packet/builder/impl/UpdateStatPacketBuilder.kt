package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateStatPacket

@Singleton
class UpdateStatPacketBuilder : PacketBuilder<UpdateStatPacket>(
    opcode = 37,
    size = 6
) {
    override fun build(packet: UpdateStatPacket, buffer: RSByteBuffer) {
        buffer.writeByteSubtract(packet.level)
        buffer.writeByteSubtract(packet.id)
        buffer.writeIntLittleMiddleEndian(packet.xp.toInt())
    }
}
