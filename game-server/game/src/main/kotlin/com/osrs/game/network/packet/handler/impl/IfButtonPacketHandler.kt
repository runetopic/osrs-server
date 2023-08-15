package com.osrs.game.network.packet.handler.impl

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.api.ui.InterfaceInfoMap
import com.osrs.api.util.interfaceId
import com.osrs.api.util.childId
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.IfButtonPacket
import com.osrs.game.ui.UserInterfaceEvent

@Singleton
class IfButtonPacketHandler @Inject constructor(
    private val interfaceInfoMap: InterfaceInfoMap,
) : PacketHandler<IfButtonPacket>() {

    private val logger = InlineLogger()

    override fun handlePacket(packet: IfButtonPacket, player: Player) {
        val interfaceId = packet.packedInterface.interfaceId()

        logger.info { "Clicked on $interfaceId "}

        val clickEvent = UserInterfaceEvent.ButtonClickEvent(
            index = packet.index,
            interfaceId = interfaceId,
            childId = packet.packedInterface.childId(),
            slotId = packet.slotId,
            itemId = packet.itemId,
            action = "*"
        )

        val info = interfaceInfoMap.findById(packet.packedInterface.interfaceId()) ?: return

        logger.info { "Info $info" }

        val listener = player.interfaces.listeners[info.id] ?: throw IllegalStateException("No listener for $interfaceId")

        logger.info { "Listener $listener" }

        logger.info { player.interfaces.listeners }

        listener.click(clickEvent)
    }
}
