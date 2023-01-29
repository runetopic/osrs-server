package com.osrs.game.tick.task.player

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.RenderType
import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.actor.render.impl.TemporaryMovementSpeed
import com.osrs.game.network.packet.server.PlayerInfoPacket
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.MovementTypeBlock
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.PlayerAppearanceBlock
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.TemporaryMovementTypeBlock
import com.osrs.game.tick.task.SyncTask
import com.osrs.game.tick.task.player.PlayerUpdateBlocks.clearUpdateBlocks
import com.osrs.game.tick.task.player.PlayerUpdateBlocks.pendingUpdateBlocks
import com.osrs.game.world.World
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.writeFully

class PlayerSyncTask(
    val world: World
) : SyncTask {

    override fun sync() {
        val players = world.players.filterNotNull().filter { it.online }

        players.forEach(Player::processGroupedPackets)
        players.forEach(Player::process)
        players.forEach(PlayerUpdateBlocks::buildPendingUpdates)
        players.forEach(Player::sendPlayerInfo)
        players.forEach(Player::reset)
        players.forEach(Player::writeAndFlush)

        clearUpdateBlocks()
    }
}
