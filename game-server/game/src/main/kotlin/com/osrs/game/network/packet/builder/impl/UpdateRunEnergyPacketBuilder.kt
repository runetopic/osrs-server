package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateRunEnergyPacket

@Singleton
class UpdateRunEnergyPacketBuilder : PacketBuilder<UpdateRunEnergyPacket>(
    opcode = 75,
    size = 2
) {
    override fun build(packet: UpdateRunEnergyPacket, buffer: RSByteBuffer) {
        buffer.writeShort(packet.energy)
    }
}
