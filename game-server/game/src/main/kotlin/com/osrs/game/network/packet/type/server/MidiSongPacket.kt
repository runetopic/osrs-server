package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class MidiSongPacket(
    val trackId: Int
) : Packet
