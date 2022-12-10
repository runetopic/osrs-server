package com.osrs.game.network.packet.builder.impl.sync.block.player.kit

import com.osrs.game.actor.render.Render

data class BodyPartCompanion(
    val gender: Render.Appearance.Gender? = null,
    val id: Int
)
