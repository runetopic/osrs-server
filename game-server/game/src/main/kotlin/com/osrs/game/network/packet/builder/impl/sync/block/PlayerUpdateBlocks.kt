package com.osrs.game.network.packet.builder.impl.sync.block

import com.google.inject.Singleton
import com.osrs.common.buffer.writeBytes
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.HighDefinitionRenderingBlock
import com.osrs.game.actor.render.LowDefinitionRenderingBlock
import com.osrs.game.world.World
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.writeShortLittleEndian

@Singleton
class PlayerUpdateBlocks(
    val highDefinitionUpdates: Array<ByteArray?> = arrayOfNulls<ByteArray?>(World.MAX_PLAYERS),
    val lowDefinitionUpdates: Array<ByteArray?> = arrayOfNulls<ByteArray?>(World.MAX_PLAYERS)
) {
    fun clear() {
        lowDefinitionUpdates.fill(null)
        highDefinitionUpdates.fill(null)
    }

    fun buildPendingUpdates(other: Player) {
        if (other.renderer.hasHighDefinitionUpdate()) {
            this.highDefinitionUpdates[other.index] = other.renderer.highDefinitionUpdates().buildHighDefinitionUpdates(other).readBytes()
        }
        this.lowDefinitionUpdates[other.index] = other.renderer.lowDefinitionUpdates().buildLowDefinitionUpdates().readBytes()
    }

    private fun Collection<HighDefinitionRenderingBlock<*, *>>.buildHighDefinitionUpdates(actor: Player): ByteReadPacket = buildPacket {
        writeMask(fold(0) { current, next -> current or next.block.mask }.let { if (it > 0xff) it or BLOCK_VALUE else it })
        associateWith { it.block.build(actor, it.renderType).readBytes() }.forEach {
            actor.renderer.setLowDefinitionRenderingBlock(it.key, it.value)
            writeBytes(it.value)
        }
    }

    private fun Collection<LowDefinitionRenderingBlock<*, *>>.buildLowDefinitionUpdates(): ByteReadPacket = buildPacket {
        writeMask(fold(0) { current, next -> current or next.block.mask }.let { if (it > 0xff) it or BLOCK_VALUE else it })
        map(LowDefinitionRenderingBlock<* ,*>::bytes).forEach {
            writeBytes(it)
        }
    }

    private fun BytePacketBuilder.writeMask(mask: Int) {
        if (mask > 0xff) writeShortLittleEndian(mask.toShort()) else writeByte(mask.toByte())
    }

    private companion object {
        const val BLOCK_VALUE = 0x40
    }
}
