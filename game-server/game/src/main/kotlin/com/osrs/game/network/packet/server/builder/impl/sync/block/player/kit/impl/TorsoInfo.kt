package com.osrs.game.network.packet.server.builder.impl.sync.block.player.kit.impl

import com.osrs.game.actor.player.Equipment
import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.kit.BodyPartCompanion
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.kit.BodyPartInfo
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.writeShort

class TorsoInfo : BodyPartInfo(index = 4) {
    override fun equipmentIndex(gender: Appearance.Gender): Int = Equipment.SLOT_CHEST
    override fun build(builder: BytePacketBuilder, kit: BodyPartCompanion) = builder.writeShort((0x100 + kit.id).toShort())
}
