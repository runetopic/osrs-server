package com.osrs.game.network.packet.builder

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.Packet

abstract class PacketBuilder<out T : Packet>(
    val opcode: Int,
    val size: Int
) {
    abstract fun build(packet: @UnsafeVariance T, buffer: RSByteBuffer)
}
