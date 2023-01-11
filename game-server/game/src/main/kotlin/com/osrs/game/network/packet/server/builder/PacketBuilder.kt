package com.osrs.game.network.packet.server.builder

import com.osrs.game.network.packet.Packet
import java.nio.ByteBuffer

abstract class PacketBuilder<out T : Packet>(
    val opcode: Int,
    val size: Int
) {
    abstract fun build(packet: @UnsafeVariance T, buffer: ByteBuffer)
}
