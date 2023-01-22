package com.osrs.game.network.packet.client.reader.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.client.MouseIdlePacket
import com.osrs.game.network.packet.client.reader.PacketReader
import io.ktor.utils.io.ByteReadChannel

@Singleton
class MouseIdlePacketReader : PacketReader<MouseIdlePacket>(
    opcode = 77,
    size = 0
) {
    private val mouseIdlePacket = MouseIdlePacket()

    override suspend fun read(readChannel: ByteReadChannel, size: Int): MouseIdlePacket = mouseIdlePacket
}
