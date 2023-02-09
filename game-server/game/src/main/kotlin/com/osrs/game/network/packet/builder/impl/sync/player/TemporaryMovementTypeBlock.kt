package com.osrs.game.network.packet.builder.impl.sync.player

import com.osrs.common.buffer.writeByteSubtract
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.impl.TemporaryMovementSpeed
import com.osrs.game.network.packet.builder.impl.sync.RenderingBlock
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class TemporaryMovementTypeBlock : RenderingBlock<Player, TemporaryMovementSpeed>(
    index = 2,
    mask = 0x200
) {
    override fun build(actor: Player, render: TemporaryMovementSpeed): ByteReadPacket = buildPacket {
        writeByteSubtract(render.type.id.toByte())
    }
}
