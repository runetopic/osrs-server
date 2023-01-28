package com.osrs.game.network.packet.client.handler.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.client.IdlePacket
import com.osrs.game.network.packet.client.handler.PacketHandler

@Singleton
class IdlePacketHandler : PacketHandler<IdlePacket>() {
    override fun handlePacket(packet: IdlePacket, player: Player) {}
}
