package com.osrs.game.network.packet.server.builder.impl.sync.block.player.impl

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.impl.MovementType
import com.osrs.game.network.packet.server.builder.impl.sync.block.RenderingBlock
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class MovementTypeBlock : RenderingBlock<Player, MovementType>(
    index = 2,
    mask = 0x400
) {
    override fun build(actor: Player, render: MovementType): ByteReadPacket = buildPacket {
        // TODO teleporting
        writeByte(if (actor.moveDirection?.runDirection != null) 2 else 1)
    }
}
