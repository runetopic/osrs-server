package com.osrs.game.actor.render

import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.actor.render.impl.TemporaryMovementSpeed
import com.osrs.game.network.packet.builder.impl.sync.RenderingBlock
import com.osrs.game.network.packet.builder.impl.sync.player.MovementTypeBlock
import com.osrs.game.network.packet.builder.impl.sync.player.appearance.PlayerAppearanceBlock
import com.osrs.game.network.packet.builder.impl.sync.player.TemporaryMovementTypeBlock

interface RenderType

fun RenderType.toBlock() : RenderingBlock<*, *> = when (this) {
    is Appearance -> PlayerAppearanceBlock()
    is MovementSpeed -> MovementTypeBlock()
    is TemporaryMovementSpeed -> TemporaryMovementTypeBlock()
    else -> throw IllegalStateException("Unhandled player block in PlayerInfo. Block was $this")
}
