package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.readStringCp1252NullTerminated
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.CheatPacket
import io.ktor.utils.io.ByteReadChannel

@Singleton
class CheatPacketReader : PacketReader<CheatPacket>(
    opcode = 52,
    size = -1
){
    override suspend fun read(readChannel: ByteReadChannel, size: Int): CheatPacket = CheatPacket(
        command = readChannel.readStringCp1252NullTerminated()
    )
}
