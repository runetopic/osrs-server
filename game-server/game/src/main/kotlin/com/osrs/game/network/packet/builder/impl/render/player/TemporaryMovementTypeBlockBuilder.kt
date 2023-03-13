package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByteSubtract
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class TemporaryMovementTypeBlockBuilder : RenderBlockBuilder<Player, MovementSpeed>(
    index = 11,
    mask = 0x200,
) {
    override fun build(actor: Player, render: MovementSpeed): ByteReadPacket = buildPacket {
        writeByteSubtract(render.type.id.toByte())
    }
}
