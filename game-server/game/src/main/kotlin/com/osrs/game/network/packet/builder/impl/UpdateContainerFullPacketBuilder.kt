package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateContainerFullPacket
import kotlin.math.min

@Singleton
class UpdateContainerFullPacketBuilder : PacketBuilder<UpdateContainerFullPacket>(
    opcode = 44,
    size = -2
) {
    override fun build(packet: UpdateContainerFullPacket, buffer: RSByteBuffer) {
        buffer.writeInt(packet.packedInterface)
        buffer.writeShort(packet.containerKey)
        buffer.writeShort(packet.items.size)
        repeat(packet.items.size) { slot ->
            val item = packet.items[slot]
            val id = item?.id ?: -1
            val amount = item?.amount ?: 0
            buffer.writeByte(min(amount, 0xff))
            if (amount >= 0xff) {
                buffer.writeInt(amount)
            }
            buffer.writeShortAdd(id + 1)
        }
    }
}
