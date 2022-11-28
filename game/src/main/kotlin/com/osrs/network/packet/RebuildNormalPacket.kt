package com.osrs.network.packet

import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.Viewport
import com.osrs.game.world.map.Location

class RebuildNormalPacket(
    val viewport: Viewport,
    val location: Location,
    val initialize: Boolean,
    val players: PlayerList
) : Packet
