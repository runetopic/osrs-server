package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeStringCp1252NullTerminated
import com.osrs.game.actor.render.type.UserNameOverride
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class UserNameOverrideBlockBuilder : RenderBlockBuilder<UserNameOverride>(
    mask = 0x1000,
    index = 4
) {
    override fun build(buffer: ByteBuffer, render: UserNameOverride) {
        buffer.writeStringCp1252NullTerminated(render.prefix)
        buffer.writeStringCp1252NullTerminated(render.infix)
        buffer.writeStringCp1252NullTerminated(render.suffix)
    }

    override fun size(render: UserNameOverride): Int {
        val prefix = render.prefix.length + 1
        val infix = render.infix.length + 1
        val suffix = render.suffix.length + 1
        return prefix + infix + suffix
    }
}
