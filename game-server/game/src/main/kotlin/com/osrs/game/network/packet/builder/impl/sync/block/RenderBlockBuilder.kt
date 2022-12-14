package com.osrs.game.network.packet.builder.impl.sync.block

import com.osrs.game.actor.Actor
import io.ktor.utils.io.core.ByteReadPacket

abstract class RenderBlockBuilder<in T : Actor>(val index: Int, val mask: Int) {
    abstract fun build(actor: @UnsafeVariance T): ByteReadPacket
}
