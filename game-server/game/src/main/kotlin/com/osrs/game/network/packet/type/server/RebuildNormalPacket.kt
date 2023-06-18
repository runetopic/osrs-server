package com.osrs.game.network.packet.type.server

import com.osrs.api.map.location.Location
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.Packet

data class RebuildNormalPacket(
    val viewport: Viewport,
    val location: Location,
    val initialize: Boolean
) : Packet
