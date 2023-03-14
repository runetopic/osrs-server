package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.fill
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeShort
import com.osrs.game.hint.HintArrow
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.HintArrowPacket
import java.nio.ByteBuffer

@Singleton
class HintArrowPacketBuilder : PacketBuilder<HintArrowPacket>(
    opcode = 78,
    size = 6
) {
    override fun build(packet: HintArrowPacket, buffer: ByteBuffer) {
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
