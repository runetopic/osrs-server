package com.osrs.game.network.packet.server.builder.impl.sync.block.player.kit.impl

import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.RenderBlock
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.kit.BodyPartCompanion
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.kit.BodyPartInfo
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.writeShort

class WeaponInfo : BodyPartInfo(index = 3) {
    override fun equipmentIndex(gender: RenderBlock.Appearance.Gender): Int = Equipment.SLOT_WEAPON
    override fun build(builder: BytePacketBuilder, kit: BodyPartCompanion) {
        builder.writeShort(0x200 + 27277)
//        builder.writeByte(kit.id.toByte())
    }
}
