package com.osrs.network.buffer

import com.runetopic.cryptography.isaac.ISAAC
import io.ktor.utils.io.ByteReadChannel

suspend fun ByteReadChannel.readUByte() = readByte().toInt() and 0xff
suspend fun ByteReadChannel.readUShort() = readShort().toInt() and 0xffff
suspend fun ByteReadChannel.readUMedium() = (readUByte() shl 16) or readUShort()

suspend fun ByteReadChannel.readPacketOpcode(isaac: ISAAC) = (readUByte() - isaac.getNext() and 0xff).let {
    if (it > Byte.MAX_VALUE) (it - (Byte.MAX_VALUE + 1) shl 8) or (readUByte() - isaac.getNext()) else it
}

suspend fun ByteReadChannel.readPacketSize(size: Int) = if (size == -1 || size == -2) {
    if (size == -1) readUByte() else readUShort()
} else size
