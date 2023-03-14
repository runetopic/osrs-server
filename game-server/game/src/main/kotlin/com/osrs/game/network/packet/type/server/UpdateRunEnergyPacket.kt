package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class UpdateRunEnergyPacket(
    val energy: Int
) : Packet
