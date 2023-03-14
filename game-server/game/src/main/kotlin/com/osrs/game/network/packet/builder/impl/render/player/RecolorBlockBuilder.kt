package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteAdd
import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeByteSubtract
import com.osrs.common.buffer.writeShortAdd
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.game.actor.render.type.Recolor
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class RecolorBlockBuilder : RenderBlockBuilder<Recolor>(
    mask = 0x400,
    index = 0
) {
    override fun build(buffer: ByteBuffer, render: Recolor) {
        buffer.writeShortLittleEndian(render.delay)
        buffer.writeShortAdd(render.duration)
        buffer.writeByteNegate(render.hue)
        buffer.writeByteAdd(render.saturation)
        buffer.writeByteSubtract(render.luminance)
        buffer.writeByteSubtract(render.opacity)
    }

    override fun size(render: Recolor): Int = 8
}
