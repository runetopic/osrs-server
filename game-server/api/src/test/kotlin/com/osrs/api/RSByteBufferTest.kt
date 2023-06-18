package com.osrs.api

import com.osrs.api.buffer.RSByteBuffer
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Jordan Abraham
 */
class RSByteBufferTest {

    @Test
    fun `test signed byte`() {
        val expected = 69
        val buffer = RSByteBuffer(ByteBuffer.allocate(1))
        buffer.writeByte(expected)
        buffer.flip()
        assertEquals(expected, buffer.readByte())
    }

    @Test
    fun `test unsigned byte`() {
        val expected = 169
        val buffer = RSByteBuffer(ByteBuffer.allocate(1))
        buffer.writeByte(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUByte())
    }

    @Test
    fun `test unsigned byte subtract`() {
        val expected = 169
        val buffer = RSByteBuffer(ByteBuffer.allocate(1))
        buffer.writeByteSubtract(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUByteSubtract())
    }

    @Test
    fun `test unsigned byte add`() {
        val expected = 169
        val buffer = RSByteBuffer(ByteBuffer.allocate(1))
        buffer.writeByteAdd(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUByteAdd())
    }

    @Test
    fun `test unsigned byte negate`() {
        val expected = 169
        val buffer = RSByteBuffer(ByteBuffer.allocate(1))
        buffer.writeByteNegate(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUByteNegate())
    }

    @Test
    fun `test signed short`() {
        val expected = 16969
        val buffer = RSByteBuffer(ByteBuffer.allocate(2))
        buffer.writeShort(expected)
        buffer.flip()
        assertEquals(expected, buffer.readShort())
    }

    @Test
    fun `test unsigned short`() {
        val expected = 56969
        val buffer = RSByteBuffer(ByteBuffer.allocate(2))
        buffer.writeShort(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUShort())
    }

    @Test
    fun `test unsigned short add`() {
        val expected = 56969
        val buffer = RSByteBuffer(ByteBuffer.allocate(2))
        buffer.writeShortAdd(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUShortAdd())
    }

    @Test
    fun `test unsigned short little endian`() {
        val expected = 56969
        val buffer = RSByteBuffer(ByteBuffer.allocate(2))
        buffer.writeShortLittleEndian(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUShortLittleEndian())
    }

    @Test
    fun `test unsigned short little endian add`() {
        val expected = 56969
        val buffer = RSByteBuffer(ByteBuffer.allocate(2))
        buffer.writeShortLittleEndianAdd(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUShortLittleEndianAdd())
    }

    @Test
    fun `test unsigned 24 bit int`() {
        val expected = 696969
        val buffer = RSByteBuffer(ByteBuffer.allocate(3))
        buffer.write24BitInt(expected)
        buffer.flip()
        assertEquals(expected, buffer.readU24BitInt())
    }

    @Test
    fun `test int`() {
        val expected = 69696969
        val buffer = RSByteBuffer(ByteBuffer.allocate(4))
        buffer.writeInt(expected)
        buffer.flip()
        assertEquals(expected, buffer.readInt())
    }

    @Test
    fun `test int little endian`() {
        val expected = 69696969
        val buffer = RSByteBuffer(ByteBuffer.allocate(4))
        buffer.writeIntLittleEndian(expected)
        buffer.flip()
        assertEquals(expected, buffer.readIntLittleEndian())
    }

    @Test
    fun `test int middle endian`() {
        val expected = 69696969
        val buffer = RSByteBuffer(ByteBuffer.allocate(4))
        buffer.writeIntMiddleEndian(expected)
        buffer.flip()
        assertEquals(expected, buffer.readIntMiddleEndian())
    }

    @Test
    fun `test int little middle endian`() {
        val expected = 69696969
        val buffer = RSByteBuffer(ByteBuffer.allocate(4))
        buffer.writeIntLittleMiddleEndian(expected)
        buffer.flip()
        assertEquals(expected, buffer.readIntLittleMiddleEndian())
    }

    @Test
    fun `test long`() {
        val expected = 696969696969696969
        val buffer = RSByteBuffer(ByteBuffer.allocate(8))
        buffer.writeLong(expected)
        buffer.flip()
        assertEquals(expected, buffer.readLong())
    }

    @Test
    fun `test bytes`() {
        val expected = byteArrayOf(43, 88, 2, 99, 56)
        val buffer = RSByteBuffer(ByteBuffer.allocate(5))
        buffer.writeBytes(expected)
        buffer.flip()
        assert(expected.contentEquals(buffer.readBytes(5)))
    }

    @Test
    fun `test bytes add`() {
        val expected = byteArrayOf(43, 88, 2, 99, 56)
        val buffer = RSByteBuffer(ByteBuffer.allocate(5))
        buffer.writeBytesAdd(expected)
        buffer.flip()
        assert(expected.contentEquals(buffer.readBytesAdd(5)))
    }

    @Test
    fun `test bytes reversed`() {
        val expected = byteArrayOf(43, 88, 2, 99, 56)
        val buffer = RSByteBuffer(ByteBuffer.allocate(5))
        buffer.writeBytesReversed(expected)
        buffer.flip()
        assert(expected.contentEquals(buffer.readBytesReversed(5)))
    }

    @Test
    fun `test bytes reversed add`() {
        val expected = byteArrayOf(43, 88, 2, 99, 56)
        val buffer = RSByteBuffer(ByteBuffer.allocate(5))
        buffer.writeBytesReversedAdd(expected)
        buffer.flip()
        assert(expected.contentEquals(buffer.readBytesReversedAdd(5)))
    }

    @Test
    fun `test unsigned short smart`() {
        val expected = 16969
        val buffer = RSByteBuffer(ByteBuffer.allocate(2))
        buffer.writeSmartByteShort(expected)
        buffer.flip()
        assertEquals(expected, buffer.readUShortSmart())
    }

    @Test
    fun `test string cp1252 null terminated`() {
        val expected = "hello tyler"
        val buffer = RSByteBuffer(ByteBuffer.allocate(expected.length + 1))
        buffer.writeStringCp1252NullTerminated(expected)
        buffer.flip()
        assertEquals(expected, buffer.readStringCp1252NullTerminated())
    }

    @Test
    fun `test string cp1252 null circumfixed`() {
        val expected = "hello tyler"
        val buffer = RSByteBuffer(ByteBuffer.allocate(1 + expected.length + 1))
        buffer.writeStringCp1252NullCircumfixed(expected)
        buffer.flip()
        assertEquals(expected, buffer.readStringCp1252NullCircumfixed())
    }
}
