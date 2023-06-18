package com.osrs.game.network.packet.builder.impl.render.player

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.api.map.location.Location
import com.osrs.game.actor.render.type.ExactMove
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

class ExactMoveBlockBuilder : RenderBlockBuilder<ExactMove>(
    mask = 0x100,
    index = 10
) {
    override fun build(buffer: RSByteBuffer, render: ExactMove) {
        val distanceX1 = render.firstLocation.x - render.currentLocation.x
        val distanceZ1 = render.firstLocation.z - render.currentLocation.z
        val distanceX2 = if (render.secondLocation != Location.None) render.secondLocation.x - render.currentLocation.x else 0
        val distanceZ2 = if (render.secondLocation != Location.None) render.secondLocation.z - render.currentLocation.z else 0

        buffer.writeByte(distanceX1)
        buffer.writeByteSubtract(distanceZ1)
        buffer.writeByteNegate(distanceX2)
        buffer.writeByteNegate(distanceZ2)
        buffer.writeShortLittleEndian(render.firstDuration)
        buffer.writeShortLittleEndianAdd(render.secondDuration)
        buffer.writeShortLittleEndianAdd(render.angle)
    }

    override fun size(render: ExactMove): Int = 10
}
