package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class VarpLargePacket(
    val id: Int,
    val value: Int
) : Packet
