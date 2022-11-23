package com.osrs.cache

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.runetopic.cache.store.Js5Store

class Cache @Inject constructor(
    val store: Js5Store
) {
    private val logger = InlineLogger()

    fun load() {
        logger.info { "Loading cache files." }
    }
}
