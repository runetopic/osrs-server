package com.osrs.game.network.packet.handler

import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.Packet

abstract class PacketHandler<out T : Packet> constructor(
    val groupId: Int = 0,
) {
    abstract fun handlePacket(packet: @UnsafeVariance T, player: Player)
}
