package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info

import com.osrs.common.buffer.writeShort
import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.type.Gender
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPart
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartBuilder
import java.nio.ByteBuffer

class ArmsBuilder : BodyPartBuilder(
    appearanceIndex = BodyPart.ARMS
) {
    override fun build(buffer: ByteBuffer, gender: Gender, part: Int) {
        buffer.writeShort(0x100 + part)
    }

    override fun equipmentIndex(gender: Gender): Int = Equipment.SLOT_TORSO
}
