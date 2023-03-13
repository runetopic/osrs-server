package com.osrs.game.actor.render

import com.osrs.game.actor.Actor
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

data class LowDefinitionRenderBlock<T : Actor, R : RenderType>(
    val renderType: RenderType,
    override val builder: RenderBlockBuilder<T, R>,
    val bytes: ByteArray,
) : RenderBlock<T, R>(builder) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LowDefinitionRenderBlock<*, *>

        if (renderType != other.renderType) return false
        if (builder != other.builder) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = renderType.hashCode()
        result = 31 * result + builder.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}
