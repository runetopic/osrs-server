package com.osrs.game.network.packet.client

import com.osrs.game.network.packet.Packet

data class MovementPacket(
    val movementType: Boolean,
    val destinationX: Int,
    val destinationZ: Int
) : Packet
