package com.osrs.game.tick.task

import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.builder.impl.sync.syncAndWritePlayers
import com.osrs.game.world.World

class PlayerSyncTask(val world: World) : SyncTask {

    override fun sync() {
        world.players.filterNotNull().filter(Player::online).let { players ->
            players.forEach(Player::syncAndWritePlayers)
            players.forEach(Player::reset)
            players.forEach(Player::writeAndFlush)
        }
    }
}
