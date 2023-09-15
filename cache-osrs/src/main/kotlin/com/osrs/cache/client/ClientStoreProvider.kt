package com.osrs.cache.client

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Provider
import com.osrs.cache.server.ServerStoreProvider
import com.osrs.config.ServerConfig
import io.netty.buffer.ByteBufAllocator
import org.openrs2.cache.Store
import java.io.FileNotFoundException
import kotlin.io.path.exists

class ClientStoreProvider @Inject constructor(
    val config: ServerConfig,
    private val alloc: ByteBufAllocator,
    private val serverStoreProvider: ServerStoreProvider
): Provider<Store> {

    private val serverCachePath = config.cache.server
    private val clientCache = config.cache.client

    private val logger = InlineLogger()

    override fun get(): Store {
        copyVanillaCache()
        return Store.open(clientCache, alloc)
    }

    private fun copyVanillaCache() {
        val serverCacheFile = serverCachePath.toFile()

        if (!serverCachePath.exists()) {
            serverCacheFile.mkdirs()
        }

        val fileCount = serverCacheFile.walk().count()

        if (fileCount <= 1) {
            serverStoreProvider.downloadVanillaCache()
        }

        serverCacheFile.let { serverCache ->
            if (!serverCache.exists()) {
                throw FileNotFoundException("Server cache files not found. \n Server Cache: $serverCache \n Client Cache: $clientCache")
            }

            clientCache.toFile().let { js5Cache ->
                if (js5Cache.exists() && js5Cache.walk().count() > 1) {
                    return
                }

                logger.info { "Copying cache from server $serverCachePath for client cache: $clientCache." }
                js5Cache.mkdirs()
                serverCache.copyRecursively(js5Cache)
            }
        }
    }
}
