package com.osrs.game.actor.render

import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.actor.render.impl.TemporaryMovementSpeed
import com.osrs.game.network.packet.server.builder.impl.sync.block.RenderingBlock
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.MovementTypeBlock
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.PlayerAppearanceBlock
import com.osrs.game.network.packet.server.builder.impl.sync.block.player.TemporaryMovementTypeBlock

interface RenderType

fun RenderType.toBlock() : RenderingBlock<*, *> = when (this) {
    is Appearance -> PlayerAppearanceBlock()
    is MovementSpeed -> MovementTypeBlock()
    is TemporaryMovementSpeed -> TemporaryMovementTypeBlock()
    else -> throw IllegalStateException("Unhandled player block in PlayerInfo. Block was $this")
}
