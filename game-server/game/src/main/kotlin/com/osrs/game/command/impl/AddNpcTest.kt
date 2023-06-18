package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener

@Singleton
class AddNpcTest : CommandListener(
    name = "add_npc"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
        val npc = NPC(1, player.world, player.location)
        player.world.requestAddNpc(npc)
    }
}
