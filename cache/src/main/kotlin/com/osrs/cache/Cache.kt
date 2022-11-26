package com.osrs.cache

import com.github.michaelbull.logging.InlineLogger
import com.runetopic.cache.store.Js5Store

class Cache(
    private val store: Js5Store
) {
    val checksums = store.checksumsWithoutRSA()
    val crcs = IntArray(store.validIndexCount()) { crc(it) }

    fun load() {
        logger.info { "Loading cache files." }
    }

    fun groupReferenceTable(indexId: Int, groupId: Int): ByteArray = store.groupReferenceTable(indexId, groupId)
    fun validIndexCount() = store.validIndexCount()

    fun crc(id: Int) = store.index(id).crc

    companion object {
        private val logger = InlineLogger()
    }
}
