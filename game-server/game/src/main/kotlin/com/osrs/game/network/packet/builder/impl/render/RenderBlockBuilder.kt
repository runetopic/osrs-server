package com.osrs.game.network.packet.builder.impl.render

import com.osrs.game.actor.Actor
import com.osrs.game.actor.render.RenderType
import io.ktor.utils.io.core.ByteReadPacket

abstract class RenderBlockBuilder<out T: Actor, out R : RenderType>(
    val index: Int,
    val mask: Int
) {
    abstract fun build(actor: @UnsafeVariance T, render: @UnsafeVariance R): ByteReadPacket
}
