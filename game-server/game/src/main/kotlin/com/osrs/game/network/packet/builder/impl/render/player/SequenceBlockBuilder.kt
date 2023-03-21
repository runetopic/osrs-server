package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.render.type.Sequence
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

class SequenceBlockBuilder : RenderBlockBuilder<Sequence>(
    mask = 0x2,
    index = 8
) {
    override fun build(buffer: RSByteBuffer, render: Sequence) {
        buffer.writeShortLittleEndianAdd(render.id)
        buffer.writeByteSubtract(render.delay)
    }

    override fun size(render: Sequence): Int = 3
}
