package com.osrs.game.tick.task

import com.osrs.game.actor.NPCList
import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.processGroupedPackets
import com.osrs.game.actor.player.sendNpcInfo
import com.osrs.game.actor.player.sendPlayerInfo
import com.osrs.game.actor.player.writeAndFlush
import com.osrs.game.controller.Controller
import com.osrs.game.controller.ControllerManager.controllers
import com.osrs.game.network.packet.builder.impl.render.NPCUpdateBlocks
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.world.World
import com.osrs.game.world.map.zone.ZoneManager

class WorldSyncTask(
    val world: World,
    private val playerUpdateBlocks: PlayerUpdateBlocks,
    private val npcUpdateBlocks: NPCUpdateBlocks
) : SyncTask {

    override fun sync(tick: Int) {
        world.processLoginRequest()
        world.processNpcRequests()

        val players = world.players
        val npcs = world.npcs

        players.processGroupedPackets()
        controllers.processControllers()
        players.processPlayers()
        npcs.processNpcs()
        players.buildPlayerUpdateBlocks()
        npcs.buildNpcUpdateBlocks()
        players.sendPlayerInfo()
        players.sendNpcInfo()
        players.resetPlayers()
        npcs.resetNpcs()
        players.writeZoneUpdates()
        players.writeAndFlush()

        world.processLogoutRequest()
    }

    private fun Array<Controller<*>?>.processControllers() {
        if (isEmpty()) return
        for (controller in this) {
            if (controller == null) continue
            controller.process(world)
        }
    }

    private fun PlayerList.writeAndFlush() {
        if (isEmpty()) return
        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            player.writeAndFlush()
        }
    }

    private fun PlayerList.processGroupedPackets() {
        if (isEmpty()) return
        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            player.processGroupedPackets()
        }
    }

    private fun PlayerList.buildPlayerUpdateBlocks() {
        if (isEmpty()) return
        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            playerUpdateBlocks.buildPendingUpdatesBlocks(player)
        }
    }

    private fun NPCList.buildNpcUpdateBlocks() {
        if (isEmpty()) return
        for (npc in parallelStream()) {
            if (npc == null || !npc.online) continue
            npcUpdateBlocks.buildPendingUpdatesBlocks(npc)
        }
    }

    private fun PlayerList.sendPlayerInfo() {
        if (isEmpty()) return
        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            player.sendPlayerInfo(playerUpdateBlocks)
        }
    }

    private fun PlayerList.sendNpcInfo() {
        if (isEmpty()) return
        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            player.sendNpcInfo(npcUpdateBlocks)
        }
    }

    private fun PlayerList.processPlayers() {
        if (isEmpty()) return
        // DO NOT CHANGE THIS FROM SYNC. players always process sync
        for (player in this) {
            if (player == null || !player.online) continue
            player.syncProcess()
        }
    }

    private fun NPCList.processNpcs() {
        if (isEmpty()) return
        // DO NOT CHANGE THIS FROM SYNC. npcs always process sync
        for (npc in this) {
            if (npc == null || !npc.online) continue
            npc.syncProcess()
        }
    }

    private fun PlayerList.writeZoneUpdates() {
        if (isEmpty()) return

        ZoneManager.clear()

        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            ZoneManager.appendObservedZone(player.zones)
        }

        ZoneManager.buildSharedZoneUpdates()

        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            for (zoneLocation in player.zones) {
                val zone = ZoneManager.zones[zoneLocation] ?: continue
                if (!zone.requiresSync()) continue
                zone.writeZoneUpdates(player)
            }
        }

        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            for (zoneLocation in player.zones) {
                val zone = ZoneManager.zones[zoneLocation] ?: continue
                if (!zone.requiresSync()) continue
                zone.clear()
            }
        }
    }

    private fun PlayerList.resetPlayers() {
        if (isEmpty()) return
        // DO NOT CHANGE THIS FROM SYNC. players always reset sync
        for (player in this) {
            if (player == null || !player.online) continue
            player.syncReset(playerUpdateBlocks.highDefinitionUpdates[player.index] != null)
        }
        playerUpdateBlocks.clear()
    }

    private fun NPCList.resetNpcs() {
        if (isEmpty()) return
        // DO NOT CHANGE THIS FROM SYNC. npcs always reset sync
        for (npc in this) {
            if (npc == null || !npc.online) continue
            npc.syncReset(npcUpdateBlocks.highDefinitionUpdates[npc.index] != null)
        }
        npcUpdateBlocks.clear()
    }
}
