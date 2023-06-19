package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener

@Singleton
class AddNpcTest : CommandListener(
    name = "add_npc"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
<<<<<<< HEAD
//        val npc = NPC(1, player.world, player.location)
//        player.world.requestAddNpc(npc)
=======
        val npc = NPC(1, player.world, player.location)
        player.world.requestAddNpc(npc)
>>>>>>> af9ba4d13b573c0ef331120b746d09ecb601c0d1
    }
}
