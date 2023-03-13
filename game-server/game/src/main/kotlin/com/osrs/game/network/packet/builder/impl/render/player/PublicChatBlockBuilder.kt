package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeByteSubtract
import com.osrs.common.buffer.writeSmartByteShort
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.PublicChat
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.core.writeShortLittleEndian

/**
 * @author Jordan Abraham
 */
class PublicChatBlockBuilder : RenderBlockBuilder<Player, PublicChat>(
    index = 2,
    mask = 0x8
) {
    override fun build(actor: Player, render: PublicChat): ByteReadPacket = buildPacket {
        writeShortLittleEndian((render.color shl 8 or render.effect).toShort())
        writeByteNegate(actor.rights.toByte())
        writeByteSubtract(0) // Auto chatting.
        writeByteSubtract((render.compressedBytes.size + 1).toByte())
        writeSmartByteShort(render.decompressedSize)
        writeFully(render.compressedBytes)
    }
}
