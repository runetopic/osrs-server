package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

class TemporaryMovementTypeBlockBuilder : RenderBlockBuilder<MovementSpeed>(
    index = 11,
    mask = 0x200,
    persisted = false
) {
    override fun build(buffer: RSByteBuffer, render: MovementSpeed) {
        buffer.writeByteSubtract(render.type.id)
    }

    override fun size(render: MovementSpeed): Int = 1
}
