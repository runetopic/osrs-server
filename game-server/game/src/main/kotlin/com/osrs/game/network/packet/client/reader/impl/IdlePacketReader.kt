package com.osrs.game.network.packet.client.reader.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.client.IdlePacket
import com.osrs.game.network.packet.client.reader.PacketReader
import io.ktor.utils.io.ByteReadChannel

@Singleton
class IdlePacketReader : PacketReader<IdlePacket>(
    opcode = 77,
    size = 0
) {
    private val idlePacket = IdlePacket()

    override suspend fun read(readChannel: ByteReadChannel, size: Int): IdlePacket = idlePacket
}
