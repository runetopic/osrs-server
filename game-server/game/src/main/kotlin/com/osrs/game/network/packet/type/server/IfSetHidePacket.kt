package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class IfSetHidePacket(
    val packed: Int,
    val hidden: Boolean
) : Packet
