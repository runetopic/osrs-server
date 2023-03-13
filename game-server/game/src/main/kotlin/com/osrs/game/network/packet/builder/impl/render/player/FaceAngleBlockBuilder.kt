package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.FaceAngle
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeShort

class FaceAngleBlockBuilder : RenderBlockBuilder<Player, FaceAngle>(
    mask = 0x80,
    index = 5,
) {
    override fun build(actor: Player, render: FaceAngle): ByteReadPacket = buildPacket {
        writeShort(render.angle.toShort())
    }
}
