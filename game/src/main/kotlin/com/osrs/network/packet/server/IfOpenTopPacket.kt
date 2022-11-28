package com.osrs.network.packet.server

import com.osrs.network.packet.Packet

data class IfOpenTopPacket(
    val interfaceId: Int
) : Packet
