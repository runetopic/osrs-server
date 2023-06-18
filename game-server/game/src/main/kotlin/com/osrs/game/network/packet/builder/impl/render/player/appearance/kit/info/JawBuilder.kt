package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.type.Gender
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPart
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartBuilder

class JawBuilder : BodyPartBuilder(
    appearanceIndex = BodyPart.JAW
) {
    override fun build(buffer: RSByteBuffer, gender: Gender, part: Int) {
        if (gender == Gender.MALE) {
            buffer.writeShort(0x100 + part)
        } else {
            buffer.writeByte(0)
        }
    }

    override fun equipmentIndex(gender: Gender): Int = if (gender == Gender.MALE) Equipment.SLOT_HEAD else Equipment.SLOT_TORSO
}
