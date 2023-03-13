package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeShortLittleEndianAdd
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.SpotAnimation
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeInt

class SpotAnimationBlockBuilder : RenderBlockBuilder<Player, SpotAnimation>(
    mask = 0x800,
    index = 6,
) {
    override fun build(actor: Player, render: SpotAnimation): ByteReadPacket = buildPacket {
        writeShortLittleEndianAdd(render.id)
        writeInt(render.packed)
    }
}
