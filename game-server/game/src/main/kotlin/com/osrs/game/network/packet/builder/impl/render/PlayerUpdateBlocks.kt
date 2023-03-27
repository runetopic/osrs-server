package com.osrs.game.network.packet.builder.impl.render

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.RenderBlock
import com.osrs.game.world.World
import java.nio.ByteBuffer

@Singleton
class PlayerUpdateBlocks(
    val highDefinitionUpdates: Array<ByteArray?> = arrayOfNulls<ByteArray?>(World.MAX_PLAYERS),
    val lowDefinitionUpdates: Array<ByteArray?> = arrayOfNulls<ByteArray?>(World.MAX_PLAYERS)
) : UpdateBlocks<Player> {
    override fun buildPendingUpdatesBlocks(actor: Player) {
        val renderer = actor.renderer
        if (!renderer.hasRenderBlockUpdate()) return

        val highDefBlocks = renderer.highDefinitionRenderBlocks
        val highDefinitionMask = highDefBlocks.calculateHighDefinitionMask(0x40)
        val highDefinitionSize = highDefBlocks.calculateHighDefinitionSize(highDefinitionMask)
        val highDefinitionBuffer = RSByteBuffer(ByteBuffer.allocate(highDefinitionSize)).also {
            it.writeMask(highDefinitionMask)
        }

        var buildLowDefBlocks = false
        for (block in highDefBlocks) {
            if (block == null) continue
            val renderType = block.renderType
            val builder = block.builder
            builder.build(highDefinitionBuffer, renderType)
            if (builder.persisted) {
                renderer.lowDefinitionRenderBlocks[builder.index] = RenderBlock(renderType, builder)
                if (!buildLowDefBlocks) {
                    buildLowDefBlocks = true
                }
            }
        }

        highDefinitionUpdates[actor.index] = highDefinitionBuffer.array()

        if (!buildLowDefBlocks) return
        val lowDefBlocks = renderer.lowDefinitionRenderBlocks
        val lowDefinitionMask = lowDefBlocks.calculateLowDefinitionMask(0x40)
        val lowDefinitionSize = lowDefBlocks.calculateLowDefinitionSize(lowDefinitionMask)
        val lowDefinitionBuffer = RSByteBuffer(ByteBuffer.allocate(lowDefinitionSize)).also {
            it.writeMask(lowDefinitionMask)
        }

        for (block in lowDefBlocks) {
            if (block == null) continue
            block.builder.build(lowDefinitionBuffer, block.renderType)
        }

        lowDefinitionUpdates[actor.index] = lowDefinitionBuffer.array()
    }

    override fun clear() {
        highDefinitionUpdates.fill(null)
    }
}
