package com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl

import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.Render
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.BodyPartInfo
import io.ktor.utils.io.core.BytePacketBuilder

class WeaponInfo : BodyPartInfo(index = 3) {
    override fun equipmentIndex(gender: Render.Appearance.Gender): Int = Equipment.SLOT_WEAPON
    override fun build(builder: BytePacketBuilder, kit: BodyPartCompanion) = builder.writeByte(kit.id.toByte())
}
