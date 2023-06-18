package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.MidiSongPacket

@Singleton
class MidiSongPacketBuilder : PacketBuilder<MidiSongPacket>(
    opcode = 39,
    size = 2
) {
    override fun build(packet: MidiSongPacket, buffer: RSByteBuffer) {
        buffer.writeShort(packet.trackId)
    }
}
