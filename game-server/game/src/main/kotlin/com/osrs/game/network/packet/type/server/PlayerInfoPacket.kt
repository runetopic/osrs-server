package com.osrs.game.network.packet.type.server

import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.Packet

data class PlayerInfoPacket(
    val viewport: Viewport,
    val players: PlayerList
) : Packet
