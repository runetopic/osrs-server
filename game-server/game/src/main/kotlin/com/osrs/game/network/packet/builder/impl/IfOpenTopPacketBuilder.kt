package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.server.IfOpenTopPacket
import xlitekt.shared.buffer.writeShortAdd
import java.nio.ByteBuffer

@Singleton
class IfOpenTopPacketBuilder : PacketBuilder<IfOpenTopPacket>(
    opcode = 10,
    size = 2
) {
    override fun build(packet: IfOpenTopPacket, writePool: ByteBuffer) {
        writePool.writeShortAdd(packet.interfaceId)
    }
}
