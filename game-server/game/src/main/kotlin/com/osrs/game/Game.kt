package com.osrs.game

import com.google.inject.Inject
<<<<<<< HEAD
import com.osrs.api.map.NPCConfigList
=======
import com.osrs.api.map.NPCSpawns
import com.osrs.api.map.location.Location
>>>>>>> af9ba4d13b573c0ef331120b746d09ecb601c0d1
import com.osrs.game.actor.npc.NPC
import com.osrs.game.world.World
import com.osrs.game.world.service.PlayerSerializationService

class Game @Inject constructor(
    private val playerSerializationService: PlayerSerializationService,
<<<<<<< HEAD
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
=======
    private val npcSpawns: NPCSpawns,
    private val world: World
) {
    fun start() {
        npcSpawns.forEach {
            val npc = NPC(
                it.id,
                world,
                Location(it.x, it.z, it.level)
>>>>>>> af9ba4d13b573c0ef331120b746d09ecb601c0d1
            )

            world.npcs.add(npc)

            npc.login()
        }
    }

    fun shutdown() {
        playerSerializationService.shutdown()
    }
}
