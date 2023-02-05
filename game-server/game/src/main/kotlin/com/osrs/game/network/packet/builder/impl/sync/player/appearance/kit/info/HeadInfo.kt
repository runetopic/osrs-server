package com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit.info

import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit.BodyPartInfo
import io.ktor.utils.io.core.BytePacketBuilder

class HeadInfo : BodyPartInfo(index = 0) {
    override fun equipmentIndex(gender: Appearance.Gender): Int = Equipment.SLOT_HEAD
    override fun build(builder: BytePacketBuilder, kit: BodyPartCompanion) = builder.writeByte(kit.id.toByte())
}
