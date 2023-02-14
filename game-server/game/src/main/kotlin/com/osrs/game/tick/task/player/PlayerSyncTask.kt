package com.osrs.game.tick.task.player

import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.builder.impl.sync.PlayerUpdateBlocks
import com.osrs.game.tick.task.SyncTask
import com.osrs.game.world.World
import com.osrs.game.world.map.zone.ZoneManager

class PlayerSyncTask(
    val world: World,
    private val zoneManager: ZoneManager,
    private val playerUpdateBlocks: PlayerUpdateBlocks
) : SyncTask {

    override fun sync() {
        val players = world.players.filterNotNull().filter { it.online }

        players.parallelStream().forEach(Player::processGroupedPackets)
        players.forEach(Player::process)
        players.parallelStream().forEach(playerUpdateBlocks::buildPendingUpdates)
        players.parallelStream().forEach { it.sendPlayerInfo(playerUpdateBlocks) }
        players.forEach(Player::reset)
        playerUpdateBlocks.clear()

        players.parallelStream().forEach { player ->
            player.zones.forEach { zoneLocation ->
                zoneManager.zones[zoneLocation]?.buildPendingZoneUpdates(player)
            }
        }

        players.parallelStream().forEach(Player::writeAndFlush)

        zoneManager.zones.forEach { zone -> zone?.processZoneUpdates() }
    }
}
