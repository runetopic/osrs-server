package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class LocRemovePacket(
    val packedOffset: Int,
    val shape: Int
) : Packet
