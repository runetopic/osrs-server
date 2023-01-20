package com.osrs.cache.entry

abstract class EntryTypeMapProvider<T> : Iterable<T> {
    private val data by lazy(::loadTypeMap)

    override fun iterator(): Iterator<T> = data.values.iterator()

    fun contains(key: Int): Boolean = data[key] != null

    operator fun get(key: Int): T? = data[key]

    abstract fun loadTypeMap(): Map<Int, T>

    val size get() = data.size
}
