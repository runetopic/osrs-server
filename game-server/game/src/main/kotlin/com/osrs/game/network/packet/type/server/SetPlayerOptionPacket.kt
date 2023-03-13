package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class SetPlayerOptionPacket(
    val option: String,
    val index: Int,
    val priority: Boolean = false,
) : Packet
