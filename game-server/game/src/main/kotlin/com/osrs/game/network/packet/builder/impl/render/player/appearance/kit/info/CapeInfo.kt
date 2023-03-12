package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info

import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartInfo
import io.ktor.utils.io.core.BytePacketBuilder

class CapeInfo : BodyPartInfo(index = 1) {
    override fun equipmentIndex(gender: Appearance.Gender): Int = Equipment.SLOT_CAPE
    override fun build(builder: BytePacketBuilder, kit: BodyPartCompanion) = builder.writeByte(kit.id.toByte())
}
