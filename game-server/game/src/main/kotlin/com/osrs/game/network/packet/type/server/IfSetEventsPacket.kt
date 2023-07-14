package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class IfSetEventsPacket(
    val packedInterface: Int,
    val startIndex: Int,
    val endIndex: Int,
    val packedEvents: Int
) : Packet
