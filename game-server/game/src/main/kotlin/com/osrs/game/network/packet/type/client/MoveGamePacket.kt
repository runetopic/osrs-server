package com.osrs.game.network.packet.type.client

import com.osrs.game.network.packet.Packet

data class MoveGamePacket(
    val movementType: Boolean,
    val destinationX: Int,
    val destinationZ: Int
) : Packet
