package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.type.Gender
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPart
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartBuilder

class OffHandBuilder : BodyPartBuilder(
    appearanceIndex = BodyPart.NONE
) {
    override fun build(buffer: RSByteBuffer, gender: Gender, part: Int) {
        buffer.writeByte(part)
    }

    override fun equipmentIndex(gender: Gender): Int = Equipment.SLOT_OFF_HAND
}
