package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit

import com.osrs.game.actor.render.type.Appearance
import java.nio.ByteBuffer

abstract class BodyPartInfo(val index: Int) {
    abstract fun equipmentIndex(gender: Appearance.Gender): Int
    abstract fun build(buffer: ByteBuffer, kit: BodyPartCompanion)
}
