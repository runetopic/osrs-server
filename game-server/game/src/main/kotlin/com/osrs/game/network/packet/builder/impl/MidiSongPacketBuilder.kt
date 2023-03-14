package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeShort
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.MidiSongPacket
import java.nio.ByteBuffer

@Singleton
class MidiSongPacketBuilder : PacketBuilder<MidiSongPacket>(
    opcode = 39,
    size = 2
) {
    override fun build(packet: MidiSongPacket, buffer: ByteBuffer) {
        buffer.writeShort(packet.trackId)
    }
}
