package com.osrs.game

import com.google.inject.Inject
import com.osrs.api.map.NPCSpawns
import com.osrs.api.map.location.Location
import com.osrs.game.actor.npc.NPC
import com.osrs.game.world.World
import com.osrs.game.world.service.PlayerSerializationService

class Game @Inject constructor(
    private val playerSerializationService: PlayerSerializationService,
    private val npcSpawns: NPCSpawns,
    private val world: World
) {
    fun start() {
        npcSpawns.forEach {
            val npc = NPC(
                it.id,
                world,
                Location(it.x, it.z, it.level)
            )

            world.npcs.add(npc)

            npc.login()
        }
    }

    fun shutdown() {
        playerSerializationService.shutdown()
    }
}
