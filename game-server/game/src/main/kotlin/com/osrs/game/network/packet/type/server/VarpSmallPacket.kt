package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class VarpSmallPacket(
    val id: Int,
    val value: Int,
) : Packet
