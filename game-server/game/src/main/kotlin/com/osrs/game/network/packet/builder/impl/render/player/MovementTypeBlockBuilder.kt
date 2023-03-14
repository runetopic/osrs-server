package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteAdd
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class MovementTypeBlockBuilder : RenderBlockBuilder<MovementSpeed>(
    index = 3,
    mask = 0x4000,
) {
    override fun build(buffer: ByteBuffer, render: MovementSpeed) {
        buffer.writeByteAdd(render.type.id)
    }

    override fun size(render: MovementSpeed): Int = 1
}
