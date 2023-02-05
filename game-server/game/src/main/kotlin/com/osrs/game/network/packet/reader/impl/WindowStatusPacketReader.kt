package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.WindowStatusPacket
import io.ktor.utils.io.ByteReadChannel

@Singleton
class WindowStatusPacketReader : PacketReader<WindowStatusPacket>(
    opcode = 10,
    size = 5
) {
    override suspend fun read(readChannel: ByteReadChannel, size: Int) : WindowStatusPacket = WindowStatusPacket(
        displayMode = readChannel.readByte().toInt(),
        width = readChannel.readShort().toInt(),
        height =  readChannel.readShort().toInt()
    )
}
