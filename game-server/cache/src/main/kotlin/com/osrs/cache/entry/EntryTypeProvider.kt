package com.osrs.cache.entry

import com.osrs.common.buffer.readInt
import com.osrs.common.buffer.readStringCp1252NullTerminated
import com.osrs.common.buffer.readUByte
import com.osrs.common.buffer.readUMedium
import java.nio.ByteBuffer

abstract class EntryTypeProvider<T> : Iterable<T> {
    private var _data: Map<Int, T>? = null

    internal val data: Map<Int, T>
        get() {
            if (_data == null) {
                _data = loadTypeMap()
            }
            return _data!!
        }

    override fun iterator(): Iterator<T> = data.values.iterator()

    fun contains(key: Int): Boolean = data.containsKey(key)

    operator fun get(key: Int): T? = data[key]

    abstract fun loadTypeMap(): Map<Int, T>

    protected fun ByteBuffer.readStringIntParameters(): Map<Int, Any> = buildMap {
        repeat(readUByte()) {
            val usingString = readUByte() == 1
            put(readUMedium(), if (usingString) readStringCp1252NullTerminated() else readInt())
        }
    }

    val size get() = data.size
}
