package com.osrs.game.network.packet.handler.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.WindowStatusPacket
import com.osrs.game.ui.InterfaceLayout
import com.osrs.game.ui.UserInterface.Companion.GameInterfaces

@Singleton
class WindowStatusPacketHandler : PacketHandler<WindowStatusPacket>() {
    override fun handlePacket(packet: WindowStatusPacket, player: Player) {
        val layout = enumValues<InterfaceLayout>().find { it.id == packet.displayMode } ?: return

        player.interfaces.let {
            it.layout = layout
            it.sendInterfaceLayout(layout)
            GameInterfaces.forEach { userInterface ->
                it += userInterface
            }
        }
    }
}
