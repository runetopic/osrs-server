package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.FaceActor
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

class FaceActorBlockBuilder : RenderBlockBuilder<FaceActor>(
    mask = 0x10,
    index = 12
) {
    override fun build(buffer: RSByteBuffer, render: FaceActor) {
        val indicator = when (render.actor) {
            is Player -> 1
            is NPC -> 0
            else -> 0xFF
        }

        buffer.writeShortLittleEndian(render.actor?.index ?: 0xFFFF)
        buffer.writeByte(indicator)
    }

    override fun size(render: FaceActor): Int = 3
}
