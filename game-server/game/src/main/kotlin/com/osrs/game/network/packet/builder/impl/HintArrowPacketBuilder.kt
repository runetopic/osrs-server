package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.hint.HintArrow
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.HintArrowPacket

@Singleton
class HintArrowPacketBuilder : PacketBuilder<HintArrowPacket>(
    opcode = 78,
    size = 6
) {
    override fun build(packet: HintArrowPacket, buffer: RSByteBuffer) {
        buffer.writeByte(packet.type.id)

        when (packet.type) {
            HintArrow.PLAYER, HintArrow.NPC -> {
                buffer.writeShort(packet.targetIndex)
                buffer.fill(3, 0)
            }
            HintArrow.LOCATION -> {
                buffer.writeShort(packet.targetX)
                buffer.writeShort(packet.targetZ)
                buffer.writeByte(packet.targetHeight)
            }
        }
    }
}
