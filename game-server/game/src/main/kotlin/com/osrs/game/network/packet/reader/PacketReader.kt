package com.osrs.game.network.packet.reader

import com.osrs.game.network.packet.Packet
import io.ktor.utils.io.ByteReadChannel

abstract class PacketReader<out T : Packet>(
    open val opcode: Int,
    open val size: Int
) {
    abstract suspend fun read(readChannel: ByteReadChannel, size: Int): T?
}
