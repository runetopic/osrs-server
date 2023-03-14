package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeShort
import com.osrs.game.actor.render.type.FaceAngle
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class FaceAngleBlockBuilder : RenderBlockBuilder<FaceAngle>(
    mask = 0x80,
    index = 5
) {
    override fun build(buffer: ByteBuffer, render: FaceAngle) {
        buffer.writeShort(render.angle)
    }

    override fun size(render: FaceAngle): Int = 2
}
