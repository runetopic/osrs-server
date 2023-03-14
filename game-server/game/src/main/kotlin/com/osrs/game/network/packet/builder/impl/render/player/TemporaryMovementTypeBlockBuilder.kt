package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteSubtract
import com.osrs.game.actor.render.type.TemporaryMovementSpeed
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class TemporaryMovementTypeBlockBuilder : RenderBlockBuilder<TemporaryMovementSpeed>(
    index = 11,
    mask = 0x200
) {
    override fun build(buffer: ByteBuffer, render: TemporaryMovementSpeed) {
        buffer.writeByteSubtract(render.type.id)
    }

    override fun size(render: TemporaryMovementSpeed): Int = 1
}
