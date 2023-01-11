package com.osrs.cache

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.cache.entry.EntryTypeProvider
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.Js5Store
import kotlin.system.measureTimeMillis

class Cache @Inject constructor(
    private val store: Js5Store,
    private val entryTypeProviders: Set<EntryTypeProvider<*, *>>
) {
    val checksums = store.checksumsWithoutRSA()
    val crcs = IntArray(store.validIndexCount()) { store.index(it).crc }

    fun load() {
        val time = measureTimeMillis {
            entryTypeProviders.forEach(EntryTypeProvider<*, *>::loadEntryTypeMap)
        }
        logger.info { "Loading cache files in $time ms." }
    }

    fun groupReferenceTable(indexId: Int, groupId: Int): ByteArray = store.groupReferenceTable(indexId, groupId)
    fun validIndexCount() = store.validIndexCount()
    fun index(id: Int): Index = store.index(id)

    companion object {
        private val logger = InlineLogger()
    }
}
