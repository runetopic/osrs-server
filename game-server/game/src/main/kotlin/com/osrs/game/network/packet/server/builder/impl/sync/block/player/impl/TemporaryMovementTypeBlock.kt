package com.osrs.game.network.packet.server.builder.impl.sync.block.player.impl

import com.osrs.common.buffer.writeShortLittleEndianAdd
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.impl.TemporaryMovementType
import com.osrs.game.network.packet.server.builder.impl.sync.block.RenderingBlock
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class TemporaryMovementTypeBlock : RenderingBlock<Player, TemporaryMovementType>(
    index = 0,
    mask = 0x80
) {
    override fun build(actor: Player, render: TemporaryMovementType): ByteReadPacket = buildPacket {
        writeShortLittleEndianAdd(if (actor.moveDirection?.runDirection != null) 2 else 1)
    }
}
