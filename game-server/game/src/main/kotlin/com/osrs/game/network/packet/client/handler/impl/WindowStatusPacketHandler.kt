package com.osrs.game.network.packet.client.handler.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.client.WindowStatusPacket
import com.osrs.game.network.packet.client.handler.PacketHandler
import com.osrs.game.network.packet.server.IfOpenTopPacket
import com.osrs.game.ui.InterfaceLayout

@Singleton
class WindowStatusPacketHandler : PacketHandler<WindowStatusPacket>() {
    override fun handlePacket(packet: WindowStatusPacket, player: Player) {
        val layout = enumValues<InterfaceLayout>().find { it.id == packet.displayMode } ?: return
        player.interfaces.layout = layout
        player.write(IfOpenTopPacket(layout.interfaceId))
    }
}
