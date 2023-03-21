package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.render.type.Gender

abstract class BodyPartBuilder(
    val appearanceIndex: Int
) {
    abstract fun build(buffer: RSByteBuffer, gender: Gender, part: Int)
    abstract fun equipmentIndex(gender: Gender): Int
}
