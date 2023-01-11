package com.osrs.game.network.packet.server.builder.impl.sync.block

import com.osrs.game.actor.Actor
import io.ktor.utils.io.core.ByteReadPacket

abstract class RenderBlockBuilder<T : Actor>(val index: Int, val mask: Int) {
    abstract fun build(actor: T): ByteReadPacket
}
