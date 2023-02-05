package com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit

import com.osrs.game.actor.render.impl.Appearance
import io.ktor.utils.io.core.BytePacketBuilder

abstract class BodyPartInfo(val index: Int) {
    abstract fun equipmentIndex(gender: Appearance.Gender): Int
    abstract fun build(builder: BytePacketBuilder, kit: BodyPartCompanion)
}
