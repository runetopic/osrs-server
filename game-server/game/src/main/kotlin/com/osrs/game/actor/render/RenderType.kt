package com.osrs.game.actor.render

import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.FaceAngle
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.actor.render.type.OverHeadText
import com.osrs.game.actor.render.type.PublicChat
import com.osrs.game.actor.render.type.Recolor
import com.osrs.game.actor.render.type.Sequence
import com.osrs.game.actor.render.type.SpotAnimation
import com.osrs.game.actor.render.type.UserNameOverride
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.FaceAngleBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.MovementTypeBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.OverHeadTextBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.PublicChatBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.RecolorBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.SequenceBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.SpotAnimationBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.TemporaryMovementTypeBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.UserNameOverrideBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.PlayerAppearanceBlockBuilder

interface RenderType {
    fun toBlock(): RenderBlockBuilder<*, *> = when (this) {
        is Appearance -> Renders.playerAppearanceBlockBuilder
        is MovementSpeed -> if (this.temporary) {
            Renders.temporaryMovementTypeBlockBuilder
        } else {
            Renders.movementTypeBlockBuilder
        }
        is Recolor -> Renders.recolorBlockBuilder
        is PublicChat -> Renders.publicChatBlockBuilder
        is UserNameOverride -> Renders.userNameOverrideBlockBuilder
        is FaceAngle -> Renders.faceAngleBlockBuilder
        is SpotAnimation -> Renders.spotAnimationBlockBuilder
        is OverHeadText -> Renders.overHeadTextBlockBuilder
        is Sequence -> Renders.sequenceBlockBuilder
        else -> throw IllegalStateException("Unhandled player block in PlayerInfo. Block was $this")
    }
}

private object Renders {
    val playerAppearanceBlockBuilder = PlayerAppearanceBlockBuilder()
    val movementTypeBlockBuilder = MovementTypeBlockBuilder()
    val temporaryMovementTypeBlockBuilder = TemporaryMovementTypeBlockBuilder()
    val recolorBlockBuilder = RecolorBlockBuilder()
    val publicChatBlockBuilder = PublicChatBlockBuilder()
    val userNameOverrideBlockBuilder = UserNameOverrideBlockBuilder()
    val faceAngleBlockBuilder = FaceAngleBlockBuilder()
    val spotAnimationBlockBuilder = SpotAnimationBlockBuilder()
    val overHeadTextBlockBuilder = OverHeadTextBlockBuilder()
    val sequenceBlockBuilder = SequenceBlockBuilder()
}
