package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteNegate
import com.osrs.common.buffer.writeByteSubtract
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.common.buffer.writeSmartByteShort
import com.osrs.game.actor.render.type.PublicChat
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
class PublicChatBlockBuilder : RenderBlockBuilder<PublicChat>(
    index = 2,
    mask = 0x8
) {
    override fun build(buffer: ByteBuffer, render: PublicChat) {
        buffer.writeShortLittleEndian(render.color shl 8 or render.effect)
        buffer.writeByteNegate(render.rights)
        buffer.writeByteSubtract(0) // Auto chatting.
        buffer.writeByteSubtract(render.compressedBytes.size + (if (render.decompressedSize > Byte.MAX_VALUE) 2 else 1))
        buffer.writeSmartByteShort(render.decompressedSize)
        buffer.put(render.compressedBytes)
    }

    override fun size(render: PublicChat): Int = 5 + (if (render.decompressedSize > Byte.MAX_VALUE) 2 else 1) + render.compressedBytes.size
}
