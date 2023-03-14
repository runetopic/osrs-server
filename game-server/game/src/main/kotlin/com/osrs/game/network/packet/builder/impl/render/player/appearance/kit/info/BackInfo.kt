package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info

import com.osrs.common.buffer.writeByte
import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartInfo
import java.nio.ByteBuffer

class BackInfo : BodyPartInfo(index = 1) {
    override fun equipmentIndex(gender: Appearance.Gender): Int = Equipment.SLOT_BACK
    override fun build(buffer: ByteBuffer, kit: BodyPartCompanion) {
        buffer.writeByte(kit.id)
    }
}