package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.NoTimeoutPacket
import io.ktor.utils.io.ByteReadChannel

@Singleton
class NoTimeoutPacketReader : PacketReader<NoTimeoutPacket>(
    opcode = 93,
    size = 0
){
    private val noTimeoutPacket = NoTimeoutPacket()

    override suspend fun read(readChannel: ByteReadChannel, size: Int): NoTimeoutPacket  = noTimeoutPacket
}

