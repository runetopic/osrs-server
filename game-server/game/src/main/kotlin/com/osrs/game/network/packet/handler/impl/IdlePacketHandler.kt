package com.osrs.game.network.packet.handler.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.type.client.IdlePacket
import com.osrs.game.network.packet.handler.PacketHandler

@Singleton
class IdlePacketHandler : PacketHandler<IdlePacket>() {
    override fun handlePacket(packet: IdlePacket, player: Player) {}
}
