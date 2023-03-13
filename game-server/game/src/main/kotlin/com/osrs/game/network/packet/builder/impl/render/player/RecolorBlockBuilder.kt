package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteAdd
import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeByteSubtract
import com.osrs.common.buffer.writeShortAdd
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.Recolor
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeShortLittleEndian

class RecolorBlockBuilder : RenderBlockBuilder<Player, Recolor>(
    mask = 0x400,
    index = 0,
) {
    override fun build(actor: Player, render: Recolor): ByteReadPacket = buildPacket {
        writeShortLittleEndian(render.delay.toShort())
        writeShortAdd(render.duration.toShort())
        writeByteNegate(render.hue.toByte())
        writeByteAdd(render.saturation.toByte())
        writeByteSubtract(render.luminance.toByte())
        writeByteSubtract(render.opacity.toByte())
    }
}
