package com.osrs.game.network.packet.type.server

import com.osrs.game.actor.render.HintArrowType
import com.osrs.game.network.packet.Packet

data class HintArrowPacket(
    val type: HintArrowType,
    val targetIndex: Int,
    val targetX: Int,
    val targetZ: Int,
    val targetHeight: Int
) : Packet {
    constructor(type: HintArrowType, index: Int) : this(type, index, -1, -1, -1)
    constructor(type: HintArrowType, targetX: Int, targetZ: Int, targetHeight: Int) : this(type, -1, targetX, targetZ, targetHeight)
}
