package com.osrs.game.network.packet.type.server

import com.osrs.game.hint.HintArrow
import com.osrs.game.network.packet.Packet

data class HintArrowPacket(
    val type: HintArrow,
    val targetIndex: Int,
    val targetX: Int,
    val targetZ: Int,
    val targetHeight: Int
) : Packet {
    constructor(type: HintArrow, index: Int) : this(type, index, -1, -1, -1)
    constructor(type: HintArrow, targetX: Int, targetZ: Int, targetHeight: Int) : this(type, -1, targetX, targetZ, targetHeight)
}
