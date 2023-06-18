package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.actor.render.type.HealthUpdate
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

class HealthUpdateBlockBuilder : RenderBlockBuilder<HealthUpdate>(
    mask = 0x20,
    index = 9
) {
    override fun build(buffer: RSByteBuffer, render: HealthUpdate) {
        buffer.writeByteAdd(render.splats.size)

        for (splat in render.splats) {
            // TODO interacting
            buffer.writeSmartByteShort(splat.type.id)
            buffer.writeSmartByteShort(splat.amount)
            buffer.writeSmartByteShort(splat.delay)
        }

        buffer.writeByteSubtract(render.bars.size)

        for (bar in render.bars) {
            // TODO multiple bar support - for some reason this is only rendering 1 bar currently need to look into multiple
            buffer.writeSmartByteShort(bar.id)
            buffer.writeSmartByteShort(0)
            buffer.writeSmartByteShort(0)
            buffer.writeByteAdd(bar.percentage(render.source))
        }
    }

    override fun size(render: HealthUpdate): Int = 2 + calculateSplatSize(render) + calculateHitBarSize(render)

    private fun calculateSplatSize(render: HealthUpdate): Int = (render.splats.sumOf { it.type.id.smartValue + it.amount.smartValue + it.delay.smartValue })

    private fun calculateHitBarSize(render: HealthUpdate): Int = (render.bars.sumOf { it.id.smartValue + 3 })

    private val Int.smartValue get() = if (this > Byte.MAX_VALUE) 2 else 1
}
