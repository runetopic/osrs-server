package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit

import com.osrs.game.actor.render.type.Gender
import java.nio.ByteBuffer

abstract class BodyPartBuilder(
    val appearanceIndex: Int
) {
    abstract fun build(buffer: ByteBuffer, gender: Gender, part: Int)
    abstract fun equipmentIndex(gender: Gender): Int
}
