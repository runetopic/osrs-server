package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class ObjRemovePacket(
    val id: Int,
    val quantity: Int,
    val packedOffset: Int,
) : Packet
