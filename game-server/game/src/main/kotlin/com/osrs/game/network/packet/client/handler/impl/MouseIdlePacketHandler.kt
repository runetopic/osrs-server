package com.osrs.game.network.packet.client.handler.impl

import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.client.MouseIdlePacket
import com.osrs.game.network.packet.client.handler.PacketHandler

class MouseIdlePacketHandler : PacketHandler<MouseIdlePacket>() {
    override fun handlePacket(packet: MouseIdlePacket, player: Player) {}
}
