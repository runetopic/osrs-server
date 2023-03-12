package com.osrs.game.network.packet.builder.impl.render

import com.google.inject.Singleton
import com.osrs.common.buffer.writeBytes
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.RenderBlock
import com.osrs.game.actor.render.HighDefinitionRenderBlock
import com.osrs.game.actor.render.LowDefinitionRenderBlock
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

    private fun Array<HighDefinitionRenderBlock<*, *>?>.buildHighDefinitionUpdates(actor: Player): ByteReadPacket = buildPacket {
        val mask = calculateMask()

        writeMask(if (mask > 0xff) mask or BLOCK_VALUE else mask)

        for (block in this@buildHighDefinitionUpdates) {
            if (block == null) continue

            val blockData = block.builder.build(actor, block.renderType).readBytes()
            actor.renderer.setLowDefinitionRenderingBlock(block, blockData)
            writeBytes(blockData)
        }
    }

    private fun Array<LowDefinitionRenderBlock<*, *>?>.buildLowDefinitionUpdates(): ByteReadPacket = buildPacket {
        val mask = calculateMask()

        writeMask(if (mask > 0xff) mask or BLOCK_VALUE else mask)

        for (block in this@buildLowDefinitionUpdates) {
            if (block == null) continue
            writeBytes(block.bytes)
        }
    }

    private fun Array<out RenderBlock<*, *>?>.calculateMask(): Int {
        var mask = 0

        for (block in this) {
            if (block == null) continue
            mask = mask or block.builder.mask
        }
        return mask
    }

    private fun BytePacketBuilder.writeMask(mask: Int) {
        if (mask > 0xff) writeShortLittleEndian(mask.toShort()) else writeByte(mask.toByte())
    }

    private companion object {
        const val BLOCK_VALUE = 0x40
    }
}
