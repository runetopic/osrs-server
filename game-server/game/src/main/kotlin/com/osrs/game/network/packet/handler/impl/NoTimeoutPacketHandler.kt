package com.osrs.game.network.packet.handler.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.NoTimeoutPacket

@Singleton
class NoTimeoutPacketHandler : PacketHandler<NoTimeoutPacket>() {

    override fun handlePacket(packet: NoTimeoutPacket, player: Player) {
    }
}
