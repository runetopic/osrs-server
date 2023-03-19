package com.osrs.game.network.packet.type.server

import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.Packet

/**
 * @author Jordan Abraham
 */
data class NpcInfoPacket(
    val viewport: Viewport
) : Packet
