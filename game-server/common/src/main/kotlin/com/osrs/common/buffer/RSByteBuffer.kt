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

    fun readByte(): Int = buffer.get().toInt()
    fun readUByte(): Int = buffer.get().toInt() and 0xFF
    fun readUByteSubtract(): Int = readUByte() - 128 and 0xFF
    fun readUByteAdd(): Int = readUByte() + 128 and 0xFF
    fun readUByteNegate(): Int = -readUByte() and 0xFF

    fun readShort(): Int = buffer.short.toInt()
    fun readUShort(): Int = buffer.short.toInt() and 0xFFFF
    fun readUShortAdd(): Int = (readUByte() shl 8) or readUByteAdd()
    fun readUShortSubtract(): Int = (readUByte() shl 8) or readUByteSubtract()
    fun readUShortLittleEndian(): Int = readUByte() or (readUByte() shl 8)
    fun readUShortLittleEndianSubtract(): Int = readUByteSubtract() or (readUByte() shl 8)
    fun readUShortLittleEndianAdd(): Int = readUByteAdd() or (readUByte() shl 8)
    fun readU24BitInt(): Int = (readUByte() shl 16) or readUShort()

    fun readInt(): Int = buffer.int
    fun readIntLittleEndian(): Int = readUShortLittleEndian() or (readUByte() shl 16) or (readUByte() shl 24)
    fun readIntV1(): Int = readUShort() or (readUByte() shl 24) or (readUByte() shl 16)
    fun readIntV2(): Int = (readUByte() shl 16) or (readUByte() shl 24) or readUShortLittleEndian()

    fun readLong(): Long = buffer.long

    tailrec fun readIncrSmallSmart(increment: Int = readUShortSmart(), offset: Int = 0): Int {
        if (increment != Short.MAX_VALUE.toInt()) return offset + increment
        return readIncrSmallSmart(offset = offset + Short.MAX_VALUE)
    }

    fun readShortSmart(): Int {
        val peek = readUByte()
        return if (peek < 128) peek - 64 else (peek shl 8 or readUByte()) - 49152
    }

    fun readUShortSmart(): Int {
        val peek = readUByte()
        return if (peek < 128) peek else (peek shl 8 or readUByte()) - 32768
    }

    fun readUIntSmart(): Int {
        val peek = readUByte()
        return if (peek < 0) {
            ((peek shl 24) or (readUByte() shl 16) or (readUByte() shl 8) or readUByte()) and Integer.MAX_VALUE
        } else {
            (peek shl 8 or readUByte()).toShort().let { if (it == Short.MAX_VALUE) -1 else it }.toInt()
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

    fun readStringCp1252NullTerminated(): String = String(readUChars(lengthToDelimiter(0))).also {
        discard(1)
    }

    fun readStringCp1252NullCircumfixed(): String {
        if (readByte() != 0) throw IllegalArgumentException()
        return readStringCp1252NullTerminated()
    }

    fun readUChars(n: Int): CharArray = CharArray(n) { readUByte().toChar() }

    fun writeByte(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.put(value.toByte())
    }

    fun writeByteSubtract(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.put((128 - value.toByte()).toByte())
    }

    fun writeByteAdd(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.put((value.toByte() + 128).toByte())
    }

    fun writeByteNegate(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.put((-value.toByte()).toByte())
    }

    fun writeShort(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.putShort(value.toShort())
    }

    fun writeShortAdd(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.put((value shr 8).toByte())
        writeByteAdd(value)
    }

    fun writeShortLittleEndian(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.put(value.toByte())
        buffer.put((value shr 8).toByte())
    }

    fun writeShortLittleEndianAdd(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        writeByteAdd(value)
        buffer.put((value shr 8).toByte())
    }

    fun write24BitInt(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.put((value shr 16).toByte())
        buffer.putShort(value.toShort())
    }

    fun writeInt(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.putInt(value)
    }

    fun writeIntLittleEndian(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        writeShortLittleEndian(value)
        buffer.put((value shr 16).toByte())
        buffer.put((value shr 24).toByte())
    }

    fun writeIntV1(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.putShort(value.toShort())
        buffer.put((value shr 24).toByte())
        buffer.put((value shr 16).toByte())
    }

    fun writeIntV2(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.put((value shr 16).toByte())
        buffer.put((value shr 24).toByte())
        writeShortLittleEndian(value)
    }

    fun writeLong(value: Long) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        buffer.putLong(value)
    }

    fun writeBytes(bytes: ByteArray) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        for (byte in bytes) {
            writeByte(byte.toInt())
        }
    }

    fun writeBytesAdd(bytes: ByteArray) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        for (byte in bytes) {
            writeByteAdd(byte.toInt())
        }
    }

    fun writeReversedAdd(bytes: ByteArray) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        for (byte in bytes.reversedArray()) {
            writeByteAdd(byte.toInt())
        }
    }

    fun writeSmartByteShort(value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        if (value in 0..127) {
            writeByte(value)
        } else {
            writeShort(value + 32768)
        }
    }

    fun writeStringCp1252NullTerminated(value: String) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        for (byte in value.toByteArray()) {
            writeByte(byte.toInt())
        }
        writeByte(0)
    }

    fun fill(amount: Int, value: Int) {
        if (!ensureBytes()) {
            throw IllegalAccessException("Buffer must be in bytes access.")
        }
        for (index in 0 until amount) {
            buffer.put(value.toByte())
        }
    }

    fun discard(amount: Int) {
        buffer.position(buffer.position() + amount)
    }

    fun lengthToDelimiter(delimiter: Int): Int {
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
        if (ensureBits()) {
            throw IllegalAccessException("Buffer is already accessing bits.")
        }
        accessBitsIndex = buffer.position() * 8
        block.invoke()
        buffer.position((accessBitsIndex + 7) / 8)
        accessBitsIndex = -1
    }

    fun writeBits(count: Int, value: Int) {
        if (!ensureBits()) {
            throw IllegalAccessException("Buffer must be in bits access.")
        }
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

    private fun ensureBytes(): Boolean = accessBitsIndex == -1
    private fun ensureBits(): Boolean = accessBitsIndex != -1
}
