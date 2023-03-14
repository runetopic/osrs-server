package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info

import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeShort
import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartInfo
import java.nio.ByteBuffer

class JawInfo : BodyPartInfo(index = 11) {
    override fun equipmentIndex(gender: Appearance.Gender): Int =
        if (gender === Appearance.Gender.MALE) Equipment.SLOT_HEAD else Equipment.SLOT_TORSO

    override fun build(buffer: ByteBuffer, kit: BodyPartCompanion) {
        if (kit.gender === Appearance.Gender.MALE) {
            buffer.writeShort(0x100 + kit.id)
        } else {
            buffer.writeByte(0)
        }
    }
}
