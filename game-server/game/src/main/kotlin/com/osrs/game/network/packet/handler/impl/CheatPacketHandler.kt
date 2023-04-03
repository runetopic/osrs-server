package com.osrs.game.network.packet.handler.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.message
import com.osrs.game.command.CommandListener
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.CheatPacket

@Singleton
class CheatPacketHandler @Inject constructor(
    commands: Set<CommandListener>
) : PacketHandler<CheatPacket>() {

    private val commandMap = commands.associateBy { it.name }

    override fun handlePacket(packet: CheatPacket, player: Player) {
        val input = packet.command.split(" ")
        val arguments = input.drop(1)
        val command = input.firstOrNull() ?: return
        val commandListener = commandMap[command] ?: return player.message("Unhandled command ::$command ${arguments.joinToString(",")}")

        commandListener.execute(player, command, arguments)
    }
}
