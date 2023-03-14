package com.osrs.game.network.packet.handler.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.CheatPacket

@Singleton
class CheatPacketHandler @Inject constructor(
    commands: Set<CommandListener>
) : PacketHandler<CheatPacket>() {

    private val commandMap = commands.associateBy { it.name }

    override fun handlePacket(packet: CheatPacket, player: Player) {
        commandMap[packet.command]?.execute(packet.command, player)
    }
}
