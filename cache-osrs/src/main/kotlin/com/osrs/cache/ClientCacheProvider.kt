package com.osrs.cache

import com.google.inject.Inject
import com.google.inject.Provider
import com.osrs.cache.client.ClientCache
import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Cache
import org.openrs2.cache.Store

class ClientCacheProvider @Inject constructor(
    @ClientCache
    private val store: Store,
    private val alloc: ByteBufAllocator
) : Provider<Cache> {
    override fun get(): Cache {
        return Cache.open(store, alloc)
    }
}
