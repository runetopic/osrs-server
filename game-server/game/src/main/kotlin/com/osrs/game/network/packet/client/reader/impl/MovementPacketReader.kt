package com.osrs.game.network.packet.client.reader.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.readUByteAdd
import com.osrs.common.buffer.readUShortAdd
import com.osrs.common.buffer.readUShortLittleEndian
import com.osrs.game.network.packet.client.MovementPacket
import com.osrs.game.network.packet.client.reader.PacketReader
import io.ktor.utils.io.ByteReadChannel

@Singleton
class MovementPacketReader : PacketReader<MovementPacket>(
    opcode = 92,
    size = -1
) {
    override suspend fun read(readChannel: ByteReadChannel, size: Int): MovementPacket = MovementPacket(
        movementType = readChannel.readUByteAdd() == 1,
        destinationX = readChannel.readUShortLittleEndian(),
        destinationZ = readChannel.readUShortAdd()
    )
}
