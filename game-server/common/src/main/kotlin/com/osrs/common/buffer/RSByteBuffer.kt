package com.osrs.common.buffer

import java.nio.ByteBuffer
import kotlin.math.min

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
        return 128 - buffer.get().toInt() and 0xFF
    }

    fun readUByteAdd(): Int {
        checkAccessingBytes()
        return buffer.get().toInt() + 128 and 0xFF
    }

    fun readUByteNegate(): Int {
        checkAccessingBytes()
        return -buffer.get().toInt() and 0xFF
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
        return (buffer.get().toInt() and 0xFF shl 8) or (buffer.get().toInt() + 128 and 0xFF)
    }

    fun readUShortLittleEndian(): Int {
        checkAccessingBytes()
        return (buffer.get().toInt() and 0xFF) or (buffer.get().toInt() and 0xFF shl 8)
    }

    fun readUShortLittleEndianAdd(): Int {
        checkAccessingBytes()
        return (buffer.get().toInt() + 128 and 0xFF) or (buffer.get().toInt() and 0xFF shl 8)
    }

    fun readU24BitInt(): Int {
        checkAccessingBytes()
        return (buffer.get().toInt() and 0xFF shl 16) or (buffer.short.toInt() and 0xFFFF)
    }

    fun readInt(): Int {
        checkAccessingBytes()
        return buffer.int
    }

    fun readIntLittleEndian(): Int {
        checkAccessingBytes()
        return (buffer.get().toInt() and 0xFF) or (buffer.get().toInt() and 0xFF shl 8) or (buffer.get().toInt() and 0xFF shl 16) or (buffer.get().toInt() and 0xFF shl 24)
    }

    fun readIntMiddleEndian(): Int {
        checkAccessingBytes()
        return (buffer.short.toInt() and 0xFFFF) or (buffer.get().toInt() and 0xFF shl 24) or (buffer.get().toInt() and 0xFF shl 16)
    }

    fun readIntLittleMiddleEndian(): Int {
        checkAccessingBytes()
        return (buffer.get().toInt() and 0xFF shl 16) or (buffer.get().toInt() and 0xFF shl 24) or (buffer.get().toInt() and 0xFF) or (buffer.get().toInt() and 0xFF shl 8)
    }

    fun readLong(): Long {
        checkAccessingBytes()
        return buffer.long
    }

    fun readBytes(size: Int): ByteArray {
        checkAccessingBytes()
        return ByteArray(size) { buffer.get() }
    }

    fun readBytesAdd(size: Int): ByteArray {
        checkAccessingBytes()
        return ByteArray(size) { (buffer.get() + 128).toByte() }
    }

    fun readBytesReversed(size: Int): ByteArray {
        checkAccessingBytes()
        val bytes = ByteArray(size)
        for (index in size - 1 downTo 0) {
            bytes[index] = buffer.get()
        }
        return bytes
    }

    fun readBytesReversedAdd(size: Int): ByteArray {
        checkAccessingBytes()
        val bytes = ByteArray(size)
        for (index in size - 1 downTo 0) {
            bytes[index] = (buffer.get() + 128).toByte()
        }
        return bytes
    }

    tailrec fun readIncrSmallSmart(increment: Int = readUShortSmart(), offset: Int = 0): Int {
        if (increment != Short.MAX_VALUE.toInt()) return offset + increment
        return readIncrSmallSmart(offset = offset + Short.MAX_VALUE)
    }

    fun readUShortSmart(): Int {
        checkAccessingBytes()
        return if (unsignedPeek() < 128) buffer.get().toInt() and 0xFF else (buffer.short.toInt() and 0xFFFF) - 32768
    }

    fun readStringCp1252NullTerminated(): String {
        checkAccessingBytes()
        return String(readUChars(untilZero())).also {
            discard(1)
        }
    }

    fun readStringCp1252NullCircumfixed(): String {
        checkAccessingBytes()
        if (readByte() != 0) throw IllegalArgumentException()
        return readStringCp1252NullTerminated()
    }

    fun writeByte(value: Int) {
        checkAccessingBytes()
        buffer.put(value.toByte())
    }

    fun writeByteSubtract(value: Int) {
        checkAccessingBytes()
        buffer.put((128 - value).toByte())
    }

    fun writeByteAdd(value: Int) {
        checkAccessingBytes()
        buffer.put((value + 128).toByte())
    }

    fun writeByteNegate(value: Int) {
        checkAccessingBytes()
        buffer.put((-value).toByte())
    }

    fun writeShort(value: Int) {
        checkAccessingBytes()
        buffer.putShort(value.toShort())
    }

    fun writeShortAdd(value: Int) {
        checkAccessingBytes()
        buffer.put((value shr 8).toByte())
        buffer.put((value + 128).toByte())
    }

    fun writeShortLittleEndian(value: Int) {
        checkAccessingBytes()
        buffer.put(value.toByte())
        buffer.put((value shr 8).toByte())
    }

    fun writeShortLittleEndianAdd(value: Int) {
        checkAccessingBytes()
        buffer.put((value + 128).toByte())
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

    fun writeStringCp1252NullCircumfixed(value: String) {
        checkAccessingBytes()
        writeByte(0)
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

    fun position(): Int = buffer.position()

    fun position(newPosition: Int) {
        buffer.position(newPosition)
    }

    fun flip(): ByteBuffer = buffer.flip()

    fun clear() {
        buffer.clear()
    }

    fun array(): ByteArray = buffer.array()

    fun signedPeek(): Int = buffer[buffer.position()].toInt()

    fun unsignedPeek(): Int = buffer[buffer.position()].toInt() and 0xFF

    fun accessBits() {
        checkAccessingBytes()
        accessBitsIndex = buffer.position() * 8
    }

    fun rewindBits() {
        checkAccessingBits()
        buffer.position((accessBitsIndex + 7) / 8)
        accessBitsIndex = buffer.position() * 8
    }

    fun accessBytes() {
        checkAccessingBits()
        buffer.position((accessBitsIndex + 7) / 8)
        accessBitsIndex = -1
    }

    fun writeBits(count: Int, value: Int) {
        checkAccessingBits()
        writeBits(value, count, accessBitsIndex shr 3, accessBitsIndex % 8)
        accessBitsIndex += count
    }

    private tailrec fun writeBits(value: Int, remainingBits: Int, byteIndex: Int, bitIndex: Int) {
        if (remainingBits == 0) return
        val bitOffset = 8 - bitIndex
        // The maximum number of bits that can be written to the current byte.
        val bitsToWrite = min(remainingBits, bitOffset)
        val max = (1 shl bitsToWrite) - 1
        // The relevant bits from the value.
        val byteValue = (value ushr (remainingBits - bitsToWrite)) and max
        // The relevant bits in the current byte.
        val mask = max shl (bitOffset - bitsToWrite)
        // The current byte from the buffer.
        val currentValue = buffer.get(byteIndex).toInt()
        // The current byte with the new bits.
        val newValue = currentValue and mask.inv() or (byteValue shl (bitOffset - bitsToWrite))
        buffer.put(byteIndex, newValue.toByte())
        return writeBits(value, remainingBits - bitsToWrite, byteIndex + 1, 0)
    }

    private tailrec fun untilZero(length: Int = 0): Int {
        if (buffer[buffer.position() + length].toInt() == 0) return length
        return untilZero(length + 1)
    }

    private fun readUChars(n: Int): CharArray = CharArray(n) { (buffer.get().toInt() and 0xFF).toChar() }

    private fun checkAccessingBytes() {
        if (accessBitsIndex == -1) return
        throw IllegalAccessException("Buffer must be in bytes access.")
    }

    private fun checkAccessingBits() {
        if (accessBitsIndex != -1) return
        throw IllegalAccessException("Buffer must be in bits access.")
    }
}
