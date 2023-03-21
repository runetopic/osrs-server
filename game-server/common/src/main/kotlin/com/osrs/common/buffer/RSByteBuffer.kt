package com.osrs.common.buffer

import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
class RSByteBuffer(
    private val buffer: ByteBuffer
) {
    constructor(array: ByteArray) : this(ByteBuffer.wrap(array))

    private var accessBitsIndex = -1

    fun readByte(): Int {
        checkAccessingBytes()
        return buffer.get().toInt()
    }

    fun readUByte(): Int {
        checkAccessingBytes()
        return buffer.get().toInt() and 0xFF
    }

    fun readUByteSubtract(): Int {
        checkAccessingBytes()
        return ((buffer.get().toInt() and 0xFF) - 128) and 0xFF
    }

    fun readUByteAdd(): Int {
        checkAccessingBytes()
        return ((buffer.get().toInt() and 0xFF) + 128) and 0xFF
    }

    fun readUByteNegate(): Int {
        checkAccessingBytes()
        return -(buffer.get().toInt() and 0xFF) and 0xFF
    }

    fun readShort(): Int {
        checkAccessingBytes()
        return buffer.short.toInt()
    }

    fun readUShort(): Int {
        checkAccessingBytes()
        return buffer.short.toInt() and 0xFFFF
    }

    fun readUShortAdd(): Int {
        checkAccessingBytes()
        return ((buffer.get().toInt() and 0xFF) shl 8) or (((buffer.get().toInt() and 0xFF) + 128) and 0xFF)
    }

    fun readUShortSubtract(): Int {
        checkAccessingBytes()
        return ((buffer.get().toInt() and 0xFF) shl 8) or (((buffer.get().toInt() and 0xFF) - 128) and 0xFF)
    }

    fun readUShortLittleEndian(): Int {
        checkAccessingBytes()
        return (buffer.get().toInt() and 0xFF) or ((buffer.get().toInt() and 0xFF) shl 8)
    }

    fun readUShortLittleEndianSubtract(): Int {
        checkAccessingBytes()
        return (((buffer.get().toInt() and 0xFF) - 128) and 0xFF) or ((buffer.get().toInt() and 0xFF) shl 8)
    }

    fun readUShortLittleEndianAdd(): Int {
        checkAccessingBytes()
        return (((buffer.get().toInt() and 0xFF) + 128) and 0xFF) or ((buffer.get().toInt() and 0xFF) shl 8)
    }

    fun readU24BitInt(): Int {
        checkAccessingBytes()
        return ((buffer.get().toInt() and 0xFF) shl 16) or (buffer.short.toInt() and 0xFFFF)
    }

    fun readInt(): Int {
        checkAccessingBytes()
        return buffer.int
    }

    fun readIntLittleEndian(): Int {
        checkAccessingBytes()
        return ((buffer.get().toInt() and 0xFF) or ((buffer.get().toInt() and 0xFF) shl 8)) or ((buffer.get().toInt() and 0xFF) shl 16) or ((buffer.get().toInt() and 0xFF) shl 24)
    }

    fun readIntMiddleEndian(): Int {
        checkAccessingBytes()
        return (buffer.short.toInt() and 0xFFFF) or ((buffer.get().toInt() and 0xFF) shl 24) or ((buffer.get().toInt() and 0xFF) shl 16)
    }

    fun readIntLittleMiddleEndian(): Int {
        checkAccessingBytes()
        return ((buffer.get().toInt() and 0xFF) shl 16) or ((buffer.get().toInt() and 0xFF) shl 24) or ((buffer.get().toInt() and 0xFF) or ((buffer.get().toInt() and 0xFF) shl 8))
    }

    fun readLong(): Long {
        checkAccessingBytes()
        return buffer.long
    }

    tailrec fun readIncrSmallSmart(increment: Int = readUShortSmart(), offset: Int = 0): Int {
        if (increment != Short.MAX_VALUE.toInt()) return offset + increment
        return readIncrSmallSmart(offset = offset + Short.MAX_VALUE)
    }

    fun readShortSmart(): Int {
        checkAccessingBytes()
        val peek = buffer.get().toInt() and 0xFF
        return if (peek < 128) peek - 64 else (peek shl 8 or (buffer.get().toInt() and 0xFF)) - 49152
    }

    fun readUShortSmart(): Int {
        checkAccessingBytes()
        val peek = buffer.get().toInt() and 0xFF
        return if (peek < 128) peek else (peek shl 8 or (buffer.get().toInt() and 0xFF)) - 32768
    }

    fun readUIntSmart(): Int {
        checkAccessingBytes()
        val peek = buffer.get().toInt() and 0xFF
        return if (peek < 0) {
            ((peek shl 24) or ((buffer.get().toInt() and 0xFF) shl 16) or ((buffer.get().toInt() and 0xFF) shl 8) or (buffer.get().toInt() and 0xFF)) and Integer.MAX_VALUE
        } else {
            (peek shl 8 or (buffer.get().toInt() and 0xFF)).toShort().let { if (it == Short.MAX_VALUE) -1 else it }.toInt()
        }
    }

    tailrec fun readVarInt(increment: Int = readByte(), offset: Int = 0): Int {
        if (increment >= 0) return offset or increment
        return readVarInt(offset = (offset or (increment and 127)) shl 7)
    }

    tailrec fun method7754(opcode: Int = readUByte(), offset: Int = 0, value: Int = 0): Int {
        if (opcode <= Byte.MAX_VALUE) return value
        return method7754(opcode, offset = offset + 7, value = value or ((opcode and 127) shl offset))
    }

    fun readStringCp1252NullTerminated(): String {
        checkAccessingBytes()
        return String(readUChars(lengthUntilDelimiter(0))).also {
            discard(1)
        }
    }

    fun readStringCp1252NullCircumfixed(): String {
        checkAccessingBytes()
        if (readByte() != 0) throw IllegalArgumentException()
        return readStringCp1252NullTerminated()
    }

    fun readUChars(n: Int): CharArray {
        checkAccessingBytes()
        return CharArray(n) { (buffer.get().toInt() and 0xFF).toChar() }
    }

    fun writeByte(value: Int) {
        checkAccessingBytes()
        buffer.put(value.toByte())
    }

    fun writeByteSubtract(value: Int) {
        checkAccessingBytes()
        buffer.put((128 - value.toByte()).toByte())
    }

    fun writeByteAdd(value: Int) {
        checkAccessingBytes()
        buffer.put((value.toByte() + 128).toByte())
    }

    fun writeByteNegate(value: Int) {
        checkAccessingBytes()
        buffer.put((-value.toByte()).toByte())
    }

    fun writeShort(value: Int) {
        checkAccessingBytes()
        buffer.putShort(value.toShort())
    }

    fun writeShortAdd(value: Int) {
        checkAccessingBytes()
        buffer.put((value shr 8).toByte())
        buffer.put((value.toByte() + 128).toByte())
    }

    fun writeShortLittleEndian(value: Int) {
        checkAccessingBytes()
        buffer.put(value.toByte())
        buffer.put((value shr 8).toByte())
    }

    fun writeShortLittleEndianAdd(value: Int) {
        checkAccessingBytes()
        buffer.put((value.toByte() + 128).toByte())
        buffer.put((value shr 8).toByte())
    }

    fun write24BitInt(value: Int) {
        checkAccessingBytes()
        buffer.put((value shr 16).toByte())
        buffer.putShort(value.toShort())
    }

    fun writeInt(value: Int) {
        checkAccessingBytes()
        buffer.putInt(value)
    }

    fun writeIntLittleEndian(value: Int) {
        checkAccessingBytes()
        buffer.put(value.toByte())
        buffer.put((value shr 8).toByte())
        buffer.put((value shr 16).toByte())
        buffer.put((value shr 24).toByte())
    }

    fun writeIntMiddleEndian(value: Int) {
        checkAccessingBytes()
        buffer.putShort(value.toShort())
        buffer.put((value shr 24).toByte())
        buffer.put((value shr 16).toByte())
    }

    fun writeIntLittleMiddleEndian(value: Int) {
        checkAccessingBytes()
        buffer.put((value shr 16).toByte())
        buffer.put((value shr 24).toByte())
        buffer.put(value.toByte())
        buffer.put((value shr 8).toByte())
    }

    fun writeLong(value: Long) {
        checkAccessingBytes()
        buffer.putLong(value)
    }

    fun writeBytes(bytes: ByteArray) {
        checkAccessingBytes()
        for (byte in bytes) {
            buffer.put(byte)
        }
    }

    fun writeBytesAdd(bytes: ByteArray) {
        checkAccessingBytes()
        for (byte in bytes) {
            buffer.put((byte + 128).toByte())
        }
    }

    fun writeBytesReversed(bytes: ByteArray) {
        checkAccessingBytes()
        for (index in bytes.size - 1 downTo 0) {
            buffer.put(bytes[index])
        }
    }

    fun writeBytesReversedAdd(bytes: ByteArray) {
        checkAccessingBytes()
        for (index in bytes.size - 1 downTo 0) {
            buffer.put((bytes[index] + 128).toByte())
        }
    }

    fun writeSmartByteShort(value: Int) {
        checkAccessingBytes()
        if (value in 0..127) {
            buffer.put(value.toByte())
        } else {
            buffer.putShort((value + 32768).toShort())
        }
    }

    fun writeStringCp1252NullTerminated(value: String) {
        checkAccessingBytes()
        for (char in value) {
            buffer.put(char.code.toByte())
        }
        writeByte(0)
    }

    fun fill(amount: Int, value: Int) {
        checkAccessingBytes()
        for (index in 0 until amount) {
            buffer.put(value.toByte())
        }
    }

    fun discard(amount: Int) {
        buffer.position(buffer.position() + amount)
    }

    fun lengthUntilDelimiter(delimiter: Int): Int {
        var count = 0
        while (buffer[buffer.position() + count].toInt() != delimiter) {
            count++
        }
        return count
    }

    fun position(): Int = buffer.position()

    fun position(newPosition: Int) {
        buffer.position(newPosition)
    }

    fun flip(): ByteBuffer = buffer.flip()

    fun clear() {
        buffer.clear()
    }

    fun array(): ByteArray = buffer.array()

    fun bitAccess(block: () -> Unit) {
        checkAccessingBytes()
        accessBitsIndex = buffer.position() * 8
        block.invoke()
        buffer.position((accessBitsIndex + 7) / 8)
        accessBitsIndex = -1
    }

    fun writeBits(count: Int, value: Int) {
        checkAccessingBits()

        var remainingBits = count
        var byteIndex = accessBitsIndex shr 3
        var bitIndex = 8 - (accessBitsIndex and 7)
        accessBitsIndex += remainingBits

        while (remainingBits > bitIndex) {
            val max = (1 shl bitIndex) - 1
            val bitsToWrite = (value ushr (remainingBits - bitIndex)) and max
            val currentByte = buffer.get(byteIndex).toInt()
            val newByte = currentByte and max.inv() or bitsToWrite
            buffer.put(byteIndex++, newByte.toByte())
            remainingBits -= bitIndex
            bitIndex = 8
        }

        val currentByte = buffer.get(byteIndex).toInt()
        val remainingBitsMask = (1 shl remainingBits) - 1
        if (remainingBits == bitIndex) {
            val newByte = currentByte and remainingBitsMask.inv() or (value and remainingBitsMask)
            buffer.put(byteIndex, newByte.toByte())
        } else {
            val maskShift = bitIndex - remainingBits
            val mask = remainingBitsMask shl maskShift
            val clearedByte = currentByte and mask.inv()
            val bitsToWrite = value and remainingBitsMask shl maskShift
            val newByte = clearedByte or bitsToWrite
            buffer.put(byteIndex, newByte.toByte())
        }
    }

    private fun checkAccessingBytes() {
        if (accessBitsIndex == -1) return
        throw IllegalAccessException("Buffer must be in bytes access.")
    }

    private fun checkAccessingBits() {
        if (accessBitsIndex != -1) return
        throw IllegalAccessException("Buffer must be in bits access.")
    }
}
