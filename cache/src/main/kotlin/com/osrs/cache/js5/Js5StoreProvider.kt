package com.osrs.cache.js5

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Provider
import com.osrs.config.GameConfig
import org.openrs2.cache.Store
import java.io.FileNotFoundException

class Js5StoreProvider @Inject constructor(
    val config: GameConfig
): Provider<Store> {

    private val vanillaCachePath = config.cache.vanilla
    private val js5CachePath = config.cache.js5

    private val logger = InlineLogger()

    override fun get(): Store {
        copyVanillaCache()
        return Store.open(js5CachePath)
    }

    private fun copyVanillaCache() {
        vanillaCachePath.toFile().let { vanillaCache ->
            if (!vanillaCache.exists()) {
                throw FileNotFoundException("Vanilla cache files not found. \n Vanilla Cache: $vanillaCache \n Js5 Cache: $js5CachePath")
            }

            js5CachePath.toFile().let { js5Cache ->
                if (js5Cache.exists() && js5Cache.walk().count() > 1) {
                    return@let
                }

                logger.info { "Copying cache from vanilla $vanillaCachePath for js5 cache: $js5CachePath." }
                js5Cache.mkdirs()
                vanillaCache.copyRecursively(js5Cache)
            }
        }
    }
}
