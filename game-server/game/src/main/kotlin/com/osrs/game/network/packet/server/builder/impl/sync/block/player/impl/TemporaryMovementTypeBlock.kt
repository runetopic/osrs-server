package com.osrs.game.network.packet.server.builder.impl.sync.block.player.impl

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.impl.TemporaryMovementType
import com.osrs.game.network.packet.server.builder.impl.sync.block.RenderingBlock
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class TemporaryMovementTypeBlock : RenderingBlock<Player, TemporaryMovementType>(
    index = 2,
    mask = 0x800
) {
    override fun build(actor: Player, render: TemporaryMovementType): ByteReadPacket = buildPacket {
        writeByte(render.type.toByte())
    }
}
