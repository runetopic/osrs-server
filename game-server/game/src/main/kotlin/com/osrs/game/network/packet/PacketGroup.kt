package com.osrs.game.network.packet

import com.osrs.game.network.packet.client.handler.PacketHandler

class PacketGroup(
    val packet: Packet,
    val handler: PacketHandler<Packet>
)
