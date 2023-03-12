package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info

import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartInfo
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.writeShort

class JawInfo : BodyPartInfo(index = 11) {
    override fun equipmentIndex(gender: Appearance.Gender): Int =
        if (gender === Appearance.Gender.MALE) Equipment.SLOT_HEAD else Equipment.SLOT_CHEST

    override fun build(builder: BytePacketBuilder, kit: BodyPartCompanion) {
        if (kit.gender === Appearance.Gender.MALE) {
            builder.writeShort((0x100 + kit.id).toShort())
        } else {
            builder.writeByte(0)
        }
    }
}
