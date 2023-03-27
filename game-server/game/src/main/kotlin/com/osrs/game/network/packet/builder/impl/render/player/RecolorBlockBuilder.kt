package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.render.type.Recolor
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

class RecolorBlockBuilder : RenderBlockBuilder<Recolor>(
    mask = 0x400,
    index = 0,
    persisted = false
) {
    override fun build(buffer: RSByteBuffer, render: Recolor) {
        buffer.writeShortLittleEndian(render.delay)
        buffer.writeShortAdd(render.duration)
        buffer.writeByteNegate(render.hue)
        buffer.writeByteAdd(render.saturation)
        buffer.writeByteSubtract(render.luminance)
        buffer.writeByteSubtract(render.opacity)
    }

    override fun size(render: Recolor): Int = 8
}
