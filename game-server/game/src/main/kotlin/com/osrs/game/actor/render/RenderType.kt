package com.osrs.game.actor.render

import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.actor.render.type.PublicChat
import com.osrs.game.actor.render.type.Recolor
import com.osrs.game.actor.render.type.TemporaryMovementSpeed
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.MovementTypeBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.PublicChatBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.RecolorBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.TemporaryMovementTypeBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.PlayerAppearanceBlockBuilder

interface RenderType {
    fun toBlock(): RenderBlockBuilder<*> = when (this) {
        is Appearance -> Renders.playerAppearanceBlockBuilder
        is MovementSpeed -> Renders.movementTypeBlockBuilder
        is TemporaryMovementSpeed -> Renders.temporaryMovementTypeBlockBuilder
        is Recolor -> Renders.recolorBlockBuilder
        is PublicChat -> Renders.publicChatBlockBuilder
        else -> throw IllegalStateException("Unhandled player block in PlayerInfo. Block was $this")
    }
}

private object Renders {
    val playerAppearanceBlockBuilder = PlayerAppearanceBlockBuilder()
    val movementTypeBlockBuilder = MovementTypeBlockBuilder()
    val temporaryMovementTypeBlockBuilder = TemporaryMovementTypeBlockBuilder()
    val recolorBlockBuilder = RecolorBlockBuilder()
    val publicChatBlockBuilder = PublicChatBlockBuilder()
}
