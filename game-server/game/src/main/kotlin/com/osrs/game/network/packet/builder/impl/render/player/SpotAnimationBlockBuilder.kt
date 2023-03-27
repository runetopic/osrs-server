package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.render.type.SpotAnimation
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

class SpotAnimationBlockBuilder : RenderBlockBuilder<SpotAnimation>(
    mask = 0x800,
    index = 6,
    persisted = false
) {
    override fun build(buffer: RSByteBuffer, render: SpotAnimation) {
        buffer.writeShortLittleEndianAdd(render.id)
        buffer.writeInt(render.packed)
    }

    override fun size(render: SpotAnimation): Int = 6
}
