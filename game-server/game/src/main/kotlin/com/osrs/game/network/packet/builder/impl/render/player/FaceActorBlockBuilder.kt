package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.FaceActor
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import java.nio.ByteBuffer

class FaceActorBlockBuilder : RenderBlockBuilder<FaceActor>(
    mask = 0x10,
    index = 12
) {
    override fun build(buffer: ByteBuffer, render: FaceActor) {
        val index = when (render.actor) {
            is Player, is NPC -> render.actor.index
            else -> 0xFFFF
        }

        val indicator = when (render.actor) {
            is Player -> 1
            is NPC -> 0
            else -> 0xFF
        }

        buffer.writeShortLittleEndian(index)
        buffer.writeByte(indicator)
    }

    override fun size(render: FaceActor): Int = 3
}
