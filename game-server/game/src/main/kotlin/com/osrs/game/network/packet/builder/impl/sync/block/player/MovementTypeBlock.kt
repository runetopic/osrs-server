package com.osrs.game.network.packet.builder.impl.sync.block.player

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.network.packet.builder.impl.sync.block.RenderingBlock
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class MovementTypeBlock : RenderingBlock<Player, MovementSpeed>(
    index = 1,
    mask = 0x400
) {
    override fun build(actor: Player, render: MovementSpeed): ByteReadPacket = buildPacket {
        writeByte(render.type.id.toByte())
    }
}
