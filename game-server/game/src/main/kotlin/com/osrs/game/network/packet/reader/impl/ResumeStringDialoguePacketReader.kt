package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.readStringCp1252NullTerminated
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.ResumeStringDialoguePacket
import io.ktor.utils.io.ByteReadChannel

class ResumeStringDialoguePacketReader : PacketReader<ResumeStringDialoguePacket>(
    opcode = 42,
    size = -1
) {
    override suspend fun read(readChannel: ByteReadChannel, size: Int): ResumeStringDialoguePacket? {
        return ResumeStringDialoguePacket(
            input = readChannel.readStringCp1252NullTerminated()
        )
    }
}
