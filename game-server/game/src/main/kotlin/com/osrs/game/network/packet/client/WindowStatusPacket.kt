package com.osrs.game.network.packet.client

import com.osrs.game.network.packet.Packet

data class WindowStatusPacket(
    val displayMode: Int,
    val width: Int,
    val height: Int,
) : Packet
