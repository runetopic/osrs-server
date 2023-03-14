package com.osrs.game.network.packet.type.client

import com.osrs.game.network.packet.Packet

data class IfButtonPacket(
    val index: Int,
    val packedInterface: Int,
    val slotId: Int,
    val itemId: Int
) : Packet
