package com.osrs.cache.entry

import java.nio.ByteBuffer

abstract class EntryTypeMapProvider<T> : Iterable<T> {
    private val data by lazy(::loadTypeMap)

    override fun iterator(): Iterator<T> = data.values.iterator()

    fun contains(key: Int): Boolean = data.containsKey(key)

    operator fun get(key: Int): T? = data[key]

    abstract fun loadTypeMap(): Map<Int, T>

    protected fun ByteBuffer.assertEmptyAndRelease() {
        check(remaining() == 0) { "The remaining buffer is not empty. Remaining=${remaining()}" }
    }

    val size get() = data.size
}
