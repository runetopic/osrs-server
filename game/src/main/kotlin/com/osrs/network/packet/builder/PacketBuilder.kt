package com.osrs.network.packet.builder

import com.osrs.network.packet.Packet
import java.nio.ByteBuffer

abstract class PacketBuilder<out T : Packet>(
    val opcode: Int,
    val size: Int
) {
    abstract fun build(packet: @UnsafeVariance T, writePool: ByteBuffer)
}
