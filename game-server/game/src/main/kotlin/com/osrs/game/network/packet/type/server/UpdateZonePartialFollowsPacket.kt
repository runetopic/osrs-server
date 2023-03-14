package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class UpdateZonePartialFollowsPacket(
    val xInScene: Int,
    val zInScene: Int
) : Packet
