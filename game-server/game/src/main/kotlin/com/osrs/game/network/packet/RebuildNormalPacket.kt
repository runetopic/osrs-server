package com.osrs.game.network.packet

import com.osrs.common.location.Location
import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.Viewport

data class RebuildNormalPacket(
    val viewport: Viewport,
    val location: Location,
    val initialize: Boolean,
    val players: PlayerList
) : Packet
