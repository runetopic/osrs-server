package com.osrs.game.network.packet.builder.impl.render

import com.osrs.game.actor.render.RenderType
import java.nio.ByteBuffer

abstract class RenderBlockBuilder<out R : RenderType>(
    val index: Int,
    val mask: Int
) {
    abstract fun build(buffer: ByteBuffer, render: @UnsafeVariance R)
    abstract fun size(render: @UnsafeVariance R): Int
}
