package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeShortLittleEndianAdd
import com.osrs.game.actor.render.type.SpotAnimation
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class SpotAnimationBlockBuilder : RenderBlockBuilder<SpotAnimation>(
    mask = 0x800,
    index = 6
) {
    override fun build(buffer: ByteBuffer, render: SpotAnimation) {
        buffer.writeShortLittleEndianAdd(render.id)
        buffer.putInt(render.packed)
    }

    override fun size(render: SpotAnimation): Int = 6
}
