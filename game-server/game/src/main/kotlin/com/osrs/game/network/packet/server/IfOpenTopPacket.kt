package com.osrs.game.network.packet.server

import com.osrs.game.network.packet.Packet

data class IfOpenTopPacket(
    val interfaceId: Int
) : Packet
