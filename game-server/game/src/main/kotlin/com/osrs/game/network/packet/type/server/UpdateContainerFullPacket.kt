package com.osrs.game.network.packet.type.server

import com.osrs.game.item.Item
import com.osrs.game.network.packet.Packet

data class UpdateContainerFullPacket(
    val packedInterface: Int,
    val containerKey: Int,
    val items: List<Item?>
) : Packet
