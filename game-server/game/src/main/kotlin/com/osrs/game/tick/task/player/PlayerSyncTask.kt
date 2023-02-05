package com.osrs.game.tick.task.player

import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.builder.impl.sync.PlayerUpdateBlocks
import com.osrs.game.tick.task.SyncTask
import com.osrs.game.world.World

class PlayerSyncTask(
    val world: World,
    private val playerUpdateBlocks: PlayerUpdateBlocks
) : SyncTask {

    override fun sync() {
        val players = world.players.filterNotNull().filter { it.online }

        players.forEach(Player::processGroupedPackets)
        players.forEach(Player::process)
        players.forEach(playerUpdateBlocks::buildPendingUpdates)
        players.forEach { it.sendPlayerInfo(playerUpdateBlocks) }
        players.forEach(Player::reset)
        players.forEach(Player::writeAndFlush)

        playerUpdateBlocks.clear()
    }
}
