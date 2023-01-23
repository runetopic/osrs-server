package com.osrs.cache

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.cache.entry.config.enum.EnumTypeProvider
import com.osrs.cache.entry.config.location.LocationEntryProvider
import com.osrs.cache.entry.map.MapSquareEntryProvider
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.Js5Store

class Cache @Inject constructor(
    private val store: Js5Store,
    private val maps: MapSquareEntryProvider,
    private val locations: LocationEntryProvider,
    private val enums: EnumTypeProvider
) {
    val checksums = store.checksumsWithoutRSA()
    val crcs = IntArray(store.validIndexCount()) { store.index(it).crc }

    fun load() {
        logger.info { "Loaded ${maps.size} maps." }
        logger.info { "Loaded ${locations.size} locations." }
        logger.info { "Loaded ${enums.size} enums." }
    }

    fun groupReferenceTable(indexId: Int, groupId: Int): ByteArray = store.groupReferenceTable(indexId, groupId)
    fun validIndexCount() = store.validIndexCount()
    fun index(id: Int): Index = store.index(id)

    companion object {
        private val logger = InlineLogger()
    }
}
