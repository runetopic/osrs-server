package com.osrs.game.network.packet.builder.impl.sync.player

import com.osrs.common.buffer.writeByteAdd
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.network.packet.builder.impl.sync.RenderingBlock
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket

class MovementTypeBlock : RenderingBlock<Player, MovementSpeed>(
    index = 1,
    mask = 0x4000
) {
    override fun build(actor: Player, render: MovementSpeed): ByteReadPacket = buildPacket {
        writeByteAdd(render.type.id.toByte())
    }
}
