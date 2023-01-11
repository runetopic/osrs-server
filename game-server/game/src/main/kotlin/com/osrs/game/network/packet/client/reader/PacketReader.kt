package com.osrs.game.network.packet.client.reader

import com.osrs.game.network.packet.Packet
import io.ktor.utils.io.ByteReadChannel

abstract class PacketReader<out T : Packet>(
    val opcode: Int,
    val size: Int
) {
    abstract suspend fun read(readChannel: ByteReadChannel, size: Int): T?
}
