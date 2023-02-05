package com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit

import com.osrs.game.actor.render.impl.Appearance

data class BodyPartCompanion(
    val gender: Appearance.Gender? = null,
    val id: Int
)
