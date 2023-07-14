package com.osrs.game.network.packet.handler.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.api.ui.InterfaceInfoMap
import com.osrs.api.util.childId
import com.osrs.api.util.interfaceId
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.message
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.IfButtonPacket
import com.osrs.game.ui.UserInterface
import com.osrs.game.ui.listener.InterfaceListener
import kotlin.reflect.KClass

@Singleton
class IfButtonPacketHandler @Inject constructor(
    private val interfaceInfoMap: InterfaceInfoMap,
    private val interfaceListeners: Map<KClass<*>, InterfaceListener<*>>,
) : PacketHandler<IfButtonPacket>() {
    override fun handlePacket(packet: IfButtonPacket, player: Player) {
        val interfaceInfo = interfaceInfoMap.findById(packet.packedInterface.interfaceId()) ?: return
        val userInterface = player.interfaces.find { it.name == interfaceInfo.name } ?: return
        val listener = interfaceListeners[userInterface::class] ?: return

        listener.onClick(
            player,
            packet.packedInterface.childId(),
            userInterface,
        )
    }
}
