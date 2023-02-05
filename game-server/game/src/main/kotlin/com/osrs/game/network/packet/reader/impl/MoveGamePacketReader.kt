package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.readUByteAdd
import com.osrs.common.buffer.readUShortAdd
import com.osrs.common.buffer.readUShortLittleEndian
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.MoveGamePacket
import io.ktor.utils.io.ByteReadChannel

@Singleton
class MoveGamePacketReader : PacketReader<MoveGamePacket>(
    opcode = 92,
    size = -1
) {
    override suspend fun read(readChannel: ByteReadChannel, size: Int): MoveGamePacket = MoveGamePacket(
        movementType = readChannel.readUByteAdd() == 1,
        destinationX = readChannel.readUShortLittleEndian(),
        destinationZ = readChannel.readUShortAdd()
    )
}
