package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class MapProjAnimPacket(
    val id: Int,
    val distOffset: Int,
    val packedOffset: Int,
    val distanceX: Int,
    val distanceZ: Int,
    val targetIndex: Int,
    val startHeight: Int,
    val endHeight: Int,
    val angle: Int,
    val delay: Int,
    val flightTime: Int,
) : Packet
