package com.osrs.game.network.packet.handler.impl

import com.google.inject.Inject
import com.osrs.api.ui.InterfaceInfoMap
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.ResumeStringDialoguePacket
import com.osrs.game.ui.UserInterface
import com.osrs.game.ui.listener.InterfaceListener
import kotlin.reflect.KClass

class ResumeStringDialoguePacketHandler @Inject constructor(
    private val interfaceInfoMap: InterfaceInfoMap,
    private val interfaceListeners: Map<KClass<*>, InterfaceListener<UserInterface>>,
) : PacketHandler<ResumeStringDialoguePacket>() {
    override fun handlePacket(packet: ResumeStringDialoguePacket, player: Player) {
        if (packet.input.isEmpty()) return

        val input = packet.input
        player.interfaces.resumeString = input
    }
}
