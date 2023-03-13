package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeStringCp1252NullTerminated
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.UserNameOverride
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class UserNameOverrideBlockBuilder : RenderBlockBuilder<Player, UserNameOverride>(
    mask = 0x1000,
    index = 4,
) {
    override fun build(actor: Player, render: UserNameOverride): ByteReadPacket = buildPacket {
        writeStringCp1252NullTerminated(render.prefix)
        writeStringCp1252NullTerminated(render.infix)
        writeStringCp1252NullTerminated(render.suffix)
    }
}
