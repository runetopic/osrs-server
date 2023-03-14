package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info

import com.osrs.common.buffer.writeShort
import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartInfo
import java.nio.ByteBuffer

class LegInfo : BodyPartInfo(index = 7) {
    override fun equipmentIndex(gender: Appearance.Gender): Int = Equipment.SLOT_LEG
    override fun build(buffer: ByteBuffer, kit: BodyPartCompanion) {
        buffer.writeShort(0x100 + kit.id)
    }
}
