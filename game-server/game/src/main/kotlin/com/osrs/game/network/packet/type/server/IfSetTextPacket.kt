package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class IfSetTextPacket(
    val packed: Int,
    val text: String
) : Packet
