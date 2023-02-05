package com.osrs.game.network.packet.builder.impl.sync.block.player.kit

import com.osrs.game.actor.render.impl.Appearance

data class BodyPartCompanion(
    val gender: Appearance.Gender? = null,
    val id: Int
)
