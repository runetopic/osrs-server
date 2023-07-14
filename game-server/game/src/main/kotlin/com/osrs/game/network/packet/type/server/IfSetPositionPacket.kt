package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class IfSetPositionPacket(
    val packed: Int,
    val rawX: Int,
    val rawY: Int
) : Packet
