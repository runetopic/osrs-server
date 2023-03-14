package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class UpdateStatPacket(
    val id: Int,
    val level: Int,
    val xp: Double
) : Packet
