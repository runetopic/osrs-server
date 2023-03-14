package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeStringCp1252NullTerminated
import com.osrs.game.actor.render.type.OverHeadText
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class OverHeadTextBlockBuilder : RenderBlockBuilder<OverHeadText>(
    mask = 0x1,
    index = 7
) {
    override fun build(buffer: ByteBuffer, render: OverHeadText) {
        buffer.writeStringCp1252NullTerminated(render.text)
    }

    override fun size(render: OverHeadText): Int = render.text.length + 1
}
