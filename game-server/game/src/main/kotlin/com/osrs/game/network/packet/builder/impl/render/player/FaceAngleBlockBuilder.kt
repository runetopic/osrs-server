package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.actor.render.type.FaceAngle
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

class FaceAngleBlockBuilder : RenderBlockBuilder<FaceAngle>(
    mask = 0x80,
    index = 5
) {
    override fun build(buffer: RSByteBuffer, render: FaceAngle) {
        buffer.writeShort(render.angle)
    }

    override fun size(render: FaceAngle): Int = 2
}
