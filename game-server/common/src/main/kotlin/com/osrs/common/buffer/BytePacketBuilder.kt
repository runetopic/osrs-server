package com.osrs.common.buffer

import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.writeShort

inline fun buildPacket(block: BytePacketBuilder.() -> Unit): BytePacketBuilder {
    val builder = BytePacketBuilder()
    block.invoke(builder)
    return builder
}

fun BytePacketBuilder.writeStringCp1252NullTerminated(value: String) {
    value.chars().forEach { writeByte(it.toByte()) }
    writeByte(0)
}

fun BytePacketBuilder.writeReversed(bytes: ByteArray) {
    for (i in bytes.indices.reversed()) {
        writeByte(bytes[i])
    }
}

fun BytePacketBuilder.writeReversedAdd(bytes: ByteArray) {
    for (i in bytes.indices.reversed()) {
        writeByteAdd(bytes[i])
    }
}

fun BytePacketBuilder.writeBytes(bytes: ByteArray) {
    bytes.forEach { writeByte(it) }
}

fun BytePacketBuilder.writeBytesAdd(bytes: ByteArray) {
    bytes.forEach { writeByteAdd(it) }
}

fun BytePacketBuilder.writeMedium(value: Int) {
    writeByte((value shr 16).toByte())
    writeByte((value shr 8).toByte())
    writeByte(value.toByte())
}

fun BytePacketBuilder.writeSmart(value: Int) {
    if (value > 128) writeShort(value.toShort()) else writeByte(value.toByte())
}

fun BytePacketBuilder.writeByteNegate(value: Byte) = writeByte((0 - value).toByte())
fun BytePacketBuilder.writeByteSubtract(value: Byte) = writeByte((128 - value).toByte())
fun BytePacketBuilder.writeByteAdd(value: Byte) = writeByte((value + 128).toByte())

fun BytePacketBuilder.writeShortAdd(value: Short) {
    writeByte((value.toInt() shr 8).toByte())
    writeByteAdd(value.toByte())
}

fun BytePacketBuilder.writeShortLittleEndianAdd(value: Int) {
    writeByteAdd(value.toByte())
    writeByte((value.toInt() shr 8).toByte())
}

fun BytePacketBuilder.writeIntV1(value: Int) {
    writeByte((value shr 8).toByte())
    writeByte(value.toByte())
    writeByte((value shr 24).toByte())
    writeByte((value shr 16).toByte())
}

fun BytePacketBuilder.writeIntV2(value: Int) {
    writeByte((value shr 16).toByte())
    writeByte((value shr 24).toByte())
    writeByte(value.toByte())
    writeByte((value shr 8).toByte())
}

fun BytePacketBuilder.writeSmartByteShort(value: Int) {
    if (value in 0..127) {
        writeByte(value.toByte())
    } else {
        writeShort((value + 32768).toShort())
    }
}
