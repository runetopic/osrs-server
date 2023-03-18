package com.osrs.common.buffer

import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 * Extension functions for the ByteBuffer class for unpacking cache data.
 * Extension functions for the ByteBuffer class used for building packets.
 */
fun ByteBuffer.readStringCp1252NullTerminated() = String(readUChars(duplicate().discardUntilDelimiter(0))).also {
    discard(1)
}

fun ByteBuffer.readStringCp1252NullCircumfixed(): String {
    if (readByte() != 0) throw IllegalArgumentException()
    return readStringCp1252NullTerminated()
}

fun ByteBuffer.readUChars(n: Int) = CharArray(n) { readUByte().toChar() }

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

fun ByteBuffer.readLong(): Long {
    val f = readInt().toLong() and 0xffffffff
    val s = readInt().toLong() and 0xffffffff
    return (f shl 32) + s
}

tailrec fun ByteBuffer.readIncrSmallSmart(increment: Int = readUShortSmart(), offset: Int = 0): Int {
    if (increment != Short.MAX_VALUE.toInt()) return offset + increment
    return readIncrSmallSmart(offset = offset + Short.MAX_VALUE)
}

fun ByteBuffer.readShortSmart(): Int {
    val peek = readUByte()
    return if (peek < 128) peek - 64 else (peek shl 8 or readUByte()) - 49152
}

fun ByteBuffer.readUShortSmart(): Int {
    val peek = readUByte()
    return if (peek < 128) peek else (peek shl 8 or readUByte()) - 32768
}

fun ByteBuffer.readUIntSmart(): Int {
    val peek = readUByte()
    return if (peek < 0) {
        ((peek shl 24) or (readUByte() shl 16) or (readUByte() shl 8) or readUByte()) and Integer.MAX_VALUE
    } else {
        (peek shl 8 or readUByte()).toShort().let { if (it == Short.MAX_VALUE) -1 else it }.toInt()
    }
}

tailrec fun ByteBuffer.readVarInt(increment: Int = readByte(), offset: Int = 0): Int {
    if (increment >= 0) return offset or increment
    return readVarInt(offset = (offset or (increment and 127)) shl 7)
}

tailrec fun ByteBuffer.method7754(opcode: Int = readUByte(), offset: Int = 0, value: Int = 0): Int {
    if (opcode <= Byte.MAX_VALUE) return value
    return method7754(opcode, offset = offset + 7, value = value or ((opcode and 127) shl offset))
}

fun ByteBuffer.writeStringCp1252NullTerminated(value: String) {
    value.toByteArray().forEach(::put)
    put(0)
}

fun ByteBuffer.writeBytes(bytes: ByteArray) {
    bytes.forEach(::put)
}

fun ByteBuffer.writeBytesAdd(bytes: ByteArray) {
    bytes.forEach { writeByteAdd(it.toInt()) }
}

fun ByteBuffer.writeReversedAdd(bytes: ByteArray) {
    bytes.indices.reversed().forEach { writeByteAdd(bytes[it].toInt()) }
}

fun ByteBuffer.writeSmartByteShort(value: Int) {
    if (value in 0..127) {
        writeByte(value)
    } else {
        writeShort(value + 32768)
    }
}

fun ByteBuffer.writeByte(value: Int) {
    put(value.toByte())
}

fun ByteBuffer.writeByteSubtract(value: Int) {
    put((128 - value.toByte()).toByte())
}

fun ByteBuffer.writeByteAdd(value: Int) {
    put((value.toByte() + 128).toByte())
}

fun ByteBuffer.writeByteNegate(value: Int) {
    put((-value.toByte()).toByte())
}

fun ByteBuffer.writeShort(value: Int) {
    putShort(value.toShort())
}

fun ByteBuffer.writeShortAdd(value: Int) {
    put((value shr 8).toByte())
    writeByteAdd(value)
}

fun ByteBuffer.writeShortLittleEndian(value: Int) {
    put(value.toByte())
    put((value shr 8).toByte())
}

fun ByteBuffer.writeShortLittleEndianAdd(value: Int) {
    writeByteAdd(value)
    put((value shr 8).toByte())
}

fun ByteBuffer.write24BitInt(value: Int) {
    put((value shr 16).toByte())
    putShort(value.toShort())
}

fun ByteBuffer.writeInt(value: Int) {
    putInt(value)
}

fun ByteBuffer.writeIntLittleEndian(value: Int) {
    writeShortLittleEndian(value)
    put((value shr 16).toByte())
    put((value shr 24).toByte())
}

fun ByteBuffer.writeIntV1(value: Int) {
    putShort(value.toShort())
    put((value shr 24).toByte())
    put((value shr 16).toByte())
}

fun ByteBuffer.writeIntV2(value: Int) {
    put((value shr 16).toByte())
    put((value shr 24).toByte())
    writeShortLittleEndian(value)
}

fun ByteBuffer.fill(n: Int, value: Int) {
    repeat(n) { put(value.toByte()) }
}

fun ByteBuffer.tryPeek() = this[position()].toInt() and 0xff

fun ByteBuffer.discard(n: Int) {
    position(position() + n)
}

fun ByteBuffer.discardUntilDelimiter(delimiter: Int): Int {
    var count = 0
    while (readByte() != delimiter) {
        count++
    }
    return count
}

fun ByteBuffer.withBitAccess(bits: BitAccess) {
    position((bits.bitIndex + 7) / 8)
}

class BitAccess(val buffer: ByteBuffer) {
    var bitIndex = buffer.position() * 8

    fun writeBit(value: Boolean) {
        writeBits(1, if (value) 1 else 0)
    }

    fun writeBits(count: Int, value: Int) {
        var numBits = count

        var byteIndex = bitIndex shr 3
        var bitOffset = 8 - (bitIndex and 7)
        bitIndex += numBits

        while (numBits > bitOffset) {
            val max = masks[bitOffset]
            val tmp = buffer.get(byteIndex).toInt() and max.inv() or (value shr numBits - bitOffset and max)
            buffer.put(byteIndex++, tmp.toByte())
            numBits -= bitOffset
            bitOffset = 8
        }

        var dataValue = buffer.get(byteIndex).toInt()
        val mask = masks[numBits]
        if (numBits == bitOffset) {
            dataValue = dataValue and mask.inv() or (value and mask)
        } else {
            dataValue = dataValue and (mask shl bitOffset - numBits).inv()
            dataValue = dataValue or (value and mask shl bitOffset - numBits)
        }
        buffer.put(byteIndex, dataValue.toByte())
    }

    companion object {
        val masks = IntArray(32)

        init {
            masks.indices.forEach { masks[it] = (1 shl it) - 1 }
        }
    }
}
