package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class ObjAddPacket(
    val id: Int,
    val amount: Int,
    val packedOffset: Int
) : Packet
