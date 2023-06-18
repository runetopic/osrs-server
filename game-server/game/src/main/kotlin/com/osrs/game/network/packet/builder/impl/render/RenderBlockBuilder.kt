package com.osrs.game.network.packet.builder.impl.render

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.actor.render.RenderType

abstract class RenderBlockBuilder<out R : RenderType>(
    val index: Int,
    val mask: Int
) {
    abstract fun build(buffer: RSByteBuffer, render: @UnsafeVariance R)
    abstract fun size(render: @UnsafeVariance R): Int
}
