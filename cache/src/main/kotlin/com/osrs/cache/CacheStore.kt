package com.osrs.cache

import org.openrs2.cache.Cache
import com.osrs.cache.entry.EntryType
import io.netty.buffer.ByteBuf
import java.nio.file.Path

interface EntryTypeProvider<T : EntryType, M : MutableMap<Int, T?>> {
    fun cache(cache: Cache)
    fun ByteBuf.read(entry: T): T
    fun write(entry: T): ByteBuf
    fun clear()
}

class CacheStore(
    private val providers: Array<EntryTypeProvider<*, *>>
) {

    private var cache: Cache? = null

    fun open(path: Path) {
        cache = Cache.open(path).also {

        }
    }
}
