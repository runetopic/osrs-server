package com.osrs.game.actor.render

import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.actor.render.type.Recolor
import com.osrs.game.actor.render.type.TemporaryMovementSpeed
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.MovementTypeBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.RecolorBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.PlayerAppearanceBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.TemporaryMovementTypeBlockBuilder

interface RenderType {
    fun toBlock() : RenderBlockBuilder<*, *> = when (this) {
        is Appearance -> PlayerAppearanceBlockBuilder()
        is MovementSpeed -> MovementTypeBlockBuilder()
        is TemporaryMovementSpeed -> TemporaryMovementTypeBlockBuilder()
        is Recolor -> RecolorBlockBuilder()
        else -> throw IllegalStateException("Unhandled player block in PlayerInfo. Block was $this")
    }
}


