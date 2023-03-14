package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class IfOpenSubPacket(
    val interfaceId: Int,
    val toInterface: Int,
    val isModal: Boolean
) : Packet
