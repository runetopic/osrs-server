package com.osrs.game.network.packet.builder.impl.sync.block.player.kit

import com.osrs.game.actor.render.RenderBlock

data class BodyPartCompanion(
    val gender: RenderBlock.Appearance.Gender? = null,
    val id: Int
)
