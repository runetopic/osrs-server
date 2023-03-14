package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit

import com.osrs.game.actor.render.type.Appearance

data class BodyPartCompanion(
    val gender: Appearance.Gender? = null,
    val id: Int
)
