package com.osrs.game.network.packet.handler.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.WindowStatusPacket
import com.osrs.game.ui.GameInterfaces.gameFrame
import com.osrs.game.ui.InterfaceLayout

@Singleton
class WindowStatusPacketHandler : PacketHandler<WindowStatusPacket>() {
    override fun handlePacket(packet: WindowStatusPacket, player: Player) {
        val layout = enumValues<InterfaceLayout>().find { it.id == packet.displayMode } ?: return

        if (player.interfaces.layout != layout) {
            player.interfaces.layout = layout
        } else {
            player.interfaces.sendInterfaceLayout(layout)
            gameFrame.forEach { player.interfaces += it }
        }
    }
}
