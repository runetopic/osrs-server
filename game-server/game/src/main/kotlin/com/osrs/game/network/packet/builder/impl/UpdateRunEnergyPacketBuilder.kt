package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateRunEnergyPacket
import java.nio.ByteBuffer

@Singleton
class UpdateRunEnergyPacketBuilder : PacketBuilder<UpdateRunEnergyPacket>(
    opcode = 58,
    size = 1
) {
    override fun build(packet: UpdateRunEnergyPacket, buffer: ByteBuffer) {
        buffer.writeByte(packet.energy)
    }
}
