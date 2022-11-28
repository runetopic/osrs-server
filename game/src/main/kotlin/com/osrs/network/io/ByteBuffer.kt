package com.osrs.network.io

import java.nio.ByteBuffer

fun ByteBuffer.readByte() = get().toInt()
fun ByteBuffer.readUByte() = get().toInt() and 0xff
fun ByteBuffer.readUByteSubtract() = readUByte() - 128 and 0xff
fun ByteBuffer.readUByteAdd() = readUByte() + 128 and 0xff
fun ByteBuffer.readUByteNegate() = -readUByte() and 0xff

fun ByteBuffer.readShort() = short.toInt()
fun ByteBuffer.readUShort() = short.toInt() and 0xffff
fun ByteBuffer.readUShortAdd() = (readUByte() shl 8) or readUByteAdd()
fun ByteBuffer.readUShortSubtract() = (readUByte() shl 8) or readUByteSubtract()
fun ByteBuffer.readUShortLittleEndian() = readUByte() or (readUByte() shl 8)
fun ByteBuffer.readUShortLittleEndianSubtract() = readUByteSubtract() or (readUByte() shl 8)
fun ByteBuffer.readUShortLittleEndianAdd() = readUByteAdd() or (readUByte() shl 8)

fun ByteBuffer.readUMedium() = (readUByte() shl 16) or readUShort()

fun ByteBuffer.readInt() = int
fun ByteBuffer.readIntLittleEndian() = readUShortLittleEndian() or (readUByte() shl 16) or (readUByte() shl 24)
fun ByteBuffer.readIntV1() = readUShort() or (readUByte() shl 24) or (readUByte() shl 16)
fun ByteBuffer.readIntV2() = (readUByte() shl 16) or (readUByte() shl 24) or readUShortLittleEndian()

fun ByteBuffer.discardUntilDelimiter(delimiter: Int): Int {
    var count = 0
    while (get().toInt() != delimiter) {
        count++
    }
    return count
}

fun ByteBuffer.readUChars(n: Int) = CharArray(n) { get().toInt().toChar() }

fun ByteBuffer.readStringCp1252NullTerminated() = String(readUChars(duplicate().discardUntilDelimiter(0))).also {
    position(position() + 1)
}

fun ByteBuffer.readStringCp1252NullCircumfixed(): String {
    if (get().toInt() != 0) throw IllegalArgumentException()
    return readStringCp1252NullTerminated()
}
