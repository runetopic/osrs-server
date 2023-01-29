package com.osrs.game.tick.task.player

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.RenderType
import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.actor.render.impl.TemporaryMovementSpeed
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.MovementTypeBlock
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.PlayerAppearanceBlock
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.TemporaryMovementTypeBlock
import com.osrs.game.world.World
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.writeFully

object PlayerUpdateBlocks {
    private val pendingUpdateBlocks = arrayOfNulls<ByteArray>(World.MAX_PLAYERS)

    fun pendingUpdateBlocks() = pendingUpdateBlocks
    fun clearUpdateBlocks() = pendingUpdateBlocks.fill(null, 0)

    fun buildPendingUpdates(other: Player) {
        if (!other.renderer.hasPendingUpdate()) return

        val blocks = BytePacketBuilder()
        val updates = other.pendingUpdates().map(::mapToBlock).sortedBy { it.second.index }.toMap()
        var mask = 0x0
        updates.forEach { mask = mask or it.value.mask }
        if (mask >= 0xff) mask = mask or 0x40
        blocks.writeByte(mask.toByte())
        if (mask >= 0xff) blocks.writeByte((mask shr 8).toByte())
        updates.forEach {
            val renderType = it.key
            val block = it.value
            val data = block.build(other, renderType).readBytes()
            blocks.writeFully(data)
        }
        this.pendingUpdateBlocks[other.index] = blocks.build().readBytes()
    }

    private fun mapToBlock(it: RenderType) = when (it) {
        is Appearance -> it to PlayerAppearanceBlock()
        is MovementSpeed -> it to MovementTypeBlock()
        is TemporaryMovementSpeed -> it to TemporaryMovementTypeBlock()
        else -> throw IllegalStateException("Unhandled player block in PlayerInfo. Block was $it")
    }
}
