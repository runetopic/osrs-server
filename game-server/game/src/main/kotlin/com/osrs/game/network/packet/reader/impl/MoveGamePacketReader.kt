package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.readUByteSubtract
import com.osrs.common.buffer.readUShort
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.MoveGamePacket
import io.ktor.utils.io.ByteReadChannel

@Singleton
class MoveGamePacketReader : PacketReader<MoveGamePacket>(
    opcode = 97,
    size = -1,
) {
    override suspend fun read(readChannel: ByteReadChannel, size: Int): MoveGamePacket = MoveGamePacket(
        destinationX = readChannel.readUShort(),
        movementType = readChannel.readUByteSubtract() == 1,
        destinationZ = readChannel.readUShort(),
    )
}
