package com.osrs.game.network.packet.type.client

import com.osrs.game.network.packet.Packet

data class MovementPacket(
    val movementType: Boolean,
    val destinationX: Int,
    val destinationZ: Int
) : Packet
