package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class MessageGamePacket(
    val type: Int,
    val message: String,
    val hasPrefix: Boolean = false,
    val prefix: String = ""
) : Packet
