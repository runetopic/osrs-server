package com.osrs.game.tick.task

import com.osrs.game.actor.PlayerList
import com.osrs.game.controller.Controller
import com.osrs.game.controller.ControllerManager.controllers
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.world.World
import com.osrs.game.world.map.zone.ZoneManager

class WorldSyncTask(
    val world: World,
    private val playerUpdateBlocks: PlayerUpdateBlocks,
) : SyncTask {

    override fun sync(tick: Int) {
        world.processLoginRequest()

        val players = world.players

        players.processGroupedPackets()
        controllers.processControllers()
        players.processPlayers()
        players.buildPendingUpdateBlocks()
        players.sendPlayerInfo()
        players.resetPlayers()
        players.writeZoneUpdates()
        players.writeAndFlush()

        world.processLogoutRequest()
    }

    private fun Array<Controller<*>?>.processControllers() {
        for(controller in this) {
            if (controller == null) continue

            controller.process(world)
        }
    }

    private fun PlayerList.writeAndFlush() {
        for (player in parallelStream()) {
            if (player == null || !player.online) continue

            player.writeAndFlush()
        }
    }

    private fun PlayerList.processGroupedPackets() {
        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            player.processGroupedPackets()
        }
    }

    private fun PlayerList.buildPendingUpdateBlocks() {
        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            playerUpdateBlocks.buildPendingUpdates(player)
        }
    }

    private fun PlayerList.sendPlayerInfo() {
        for (player in parallelStream()) {
            if (player == null || !player.online) continue
            player.sendPlayerInfo(playerUpdateBlocks)
        }
    }

    private fun PlayerList.processPlayers() {
        // DO NOT CHANGE THIS FROM SYNC players always process sync
        for (player in this) {
            if (player == null || !player.online) continue
            player.process()
        }
    }

    private fun PlayerList.writeZoneUpdates() {
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

        for (zone in ZoneManager.zones) {
            if (zone == null || !zone.requiresSync()) continue

            zone.clear()
        }
    }

    private fun PlayerList.resetPlayers() {
        // DO NOT CHANGE THIS FROM SYNC players always process sync
        for (player in this) {
            if (player == null || !player.online) continue
            player.reset()
        }

        playerUpdateBlocks.clear()
    }
}
