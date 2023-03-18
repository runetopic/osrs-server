package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.write24BitInt
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeByteAdd
import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeByteSubtract
import com.osrs.common.buffer.writeShortAdd
import com.osrs.common.buffer.writeShortLittleEndianAdd
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.MapProjAnimPacket
import java.nio.ByteBuffer

@Singleton
class MapProjAnimPacketBuilder : PacketBuilder<MapProjAnimPacket>(
    opcode = 107,
    size = 16
) {
    override fun build(packet: MapProjAnimPacket, buffer: ByteBuffer) {
        buffer.writeShortLittleEndianAdd(packet.id)
        buffer.writeByte(packet.angle)
        buffer.write24BitInt(packet.targetIndex)
        buffer.writeByteSubtract(packet.distOffset)
        buffer.writeByteAdd(packet.endHeight)
        buffer.writeByteNegate(packet.startHeight)
        buffer.writeShortAdd(packet.delay)
        buffer.writeShortAdd(packet.flightTime)
        buffer.writeByteNegate(packet.distanceZ)
        buffer.writeByteSubtract(packet.distanceX)
        buffer.writeByteNegate(packet.packedOffset)
    }
}
