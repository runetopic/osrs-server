package com.osrs.cache

import com.github.michaelbull.logging.InlineLogger
import com.runetopic.cache.store.Js5Store

class Cache(
    private val store: Js5Store
) {
    val checksums = store.checksumsWithoutRSA()

    fun load() {
        logger.info { "Loading cache files." }
    }

    fun groupReferenceTable(indexId: Int, groupId: Int): ByteArray = store.groupReferenceTable(indexId, groupId)

    companion object {
        private val logger = InlineLogger()
    }
}
