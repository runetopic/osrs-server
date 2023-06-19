package com.osrs.game

import com.google.inject.Inject
import com.osrs.api.map.NPCConfigList
import com.osrs.game.actor.npc.NPC
import com.osrs.game.world.World
import com.osrs.game.world.service.PlayerSerializationService

class Game @Inject constructor(
    private val playerSerializationService: PlayerSerializationService,
    private val npcs: NPCConfigList,
    private val world: World
) {
    fun start() {
        npcs.forEach {
            val npc = NPC(
                it.id,
                it,
                world,
                it.location
            )

            world.npcs.add(npc)

            npc.login()
        }
    }

    fun shutdown() {
        playerSerializationService.shutdown()
    }
}
