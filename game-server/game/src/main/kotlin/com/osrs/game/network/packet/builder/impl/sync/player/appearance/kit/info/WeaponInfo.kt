package com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit.info

import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit.BodyPartInfo
import io.ktor.utils.io.core.BytePacketBuilder

class WeaponInfo : BodyPartInfo(index = 3) {
    override fun equipmentIndex(gender: Appearance.Gender): Int = Equipment.SLOT_WEAPON
    override fun build(builder: BytePacketBuilder, kit: BodyPartCompanion) {
//        builder.writeShort((0x200 + 27277).toShort())
        builder.writeByte(kit.id.toByte())
    }
}
