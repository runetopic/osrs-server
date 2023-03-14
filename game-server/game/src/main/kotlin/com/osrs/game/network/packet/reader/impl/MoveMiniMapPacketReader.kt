package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.readUByte
import com.osrs.common.buffer.readUByteSubtract
import com.osrs.common.buffer.readUShort
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.MoveMiniMapPacket
import io.ktor.utils.io.ByteReadChannel

@Singleton
class MoveMiniMapPacketReader : PacketReader<MoveMiniMapPacket>(
    opcode = 26,
    size = -1
) {
    override suspend fun read(readChannel: ByteReadChannel, size: Int): MoveMiniMapPacket = MoveMiniMapPacket(
        destinationX = readChannel.readUShort(),
        movementType = readChannel.readUByteSubtract(),
        destinationZ = readChannel.readUShort(),
        mouseClickedX = readChannel.readUByte(),
        mouseClickedZ = readChannel.readUByte(),
        cameraAngleZ = readChannel.readUShort(),
        value1 = readChannel.readUByte(),
        value2 = readChannel.readUByte(),
        value3 = readChannel.readUByte(),
        value4 = readChannel.readUByte(),
        currentX = readChannel.readUShort(),
        currentZ = readChannel.readUShort(),
        value5 = readChannel.readUByte()
    )
}
