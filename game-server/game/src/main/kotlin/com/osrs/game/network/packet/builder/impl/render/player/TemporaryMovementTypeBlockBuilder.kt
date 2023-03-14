package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteSubtract
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class TemporaryMovementTypeBlockBuilder : RenderBlockBuilder<MovementSpeed>(
    index = 11,
    mask = 0x200
) {
    override fun build(buffer: ByteBuffer, render: MovementSpeed) {
        buffer.writeByteSubtract(render.type.id)
    }

    override fun size(render: MovementSpeed): Int = 1
}
