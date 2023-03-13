package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeStringCp1252NullTerminated
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.OverHeadText
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class OverHeadTextBlockBuilder : RenderBlockBuilder<Player, OverHeadText>(
    mask = 0x1,
    index = 7,
) {
    override fun build(actor: Player, render: OverHeadText): ByteReadPacket = buildPacket {
        writeStringCp1252NullTerminated(render.text)
    }
}
