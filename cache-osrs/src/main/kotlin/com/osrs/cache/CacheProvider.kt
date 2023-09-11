package com.osrs.cache

import com.google.inject.Inject
import com.google.inject.Provider
import com.osrs.cache.vanilla.VanillaCache
import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Cache
import org.openrs2.cache.Store

class CacheProvider @Inject constructor(
    @VanillaCache
    private val store: Store,
    private val alloc: ByteBufAllocator
) : Provider<Cache> {
    override fun get(): Cache {
        return Cache.open(store, alloc)
    }
}
