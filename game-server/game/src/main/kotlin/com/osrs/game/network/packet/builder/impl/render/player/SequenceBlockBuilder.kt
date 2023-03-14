package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteSubtract
import com.osrs.common.buffer.writeShortLittleEndianAdd
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.Sequence
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class SequenceBlockBuilder : RenderBlockBuilder<Player, Sequence>(
    mask = 0x2,
    index = 8,
) {
    override fun build(actor: Player, render: Sequence): ByteReadPacket = buildPacket {
        writeShortLittleEndianAdd(render.id)
        writeByteSubtract(render.delay.toByte())
    }
}
