package com.osrs.game.network.packet.builder.impl.sync.block.player.kit

import com.osrs.game.actor.render.RenderBlock
import io.ktor.utils.io.core.BytePacketBuilder

abstract class BodyPartInfo(val index: Int) {
    abstract fun equipmentIndex(gender: RenderBlock.Appearance.Gender): Int
    abstract fun build(builder: BytePacketBuilder, kit: BodyPartCompanion)
}
