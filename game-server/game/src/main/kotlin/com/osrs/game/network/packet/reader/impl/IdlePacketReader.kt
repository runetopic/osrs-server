package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.IdlePacket
import io.ktor.utils.io.ByteReadChannel

@Singleton
class IdlePacketReader : PacketReader<IdlePacket>(
    opcode = 79,
    size = 0
) {
    private val idlePacket = IdlePacket()

    override suspend fun read(readChannel: ByteReadChannel, size: Int): IdlePacket = idlePacket
}
