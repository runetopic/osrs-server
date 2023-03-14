package com.osrs.cache

import com.google.inject.Inject
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.Js5Store

class Cache @Inject constructor(
    private val store: Js5Store,
) {
    val checksums = store.checksumsWithoutRSA()
    val crcs = IntArray(store.validIndexCount()) { store.index(it).crc }

    fun groupReferenceTable(indexId: Int, groupId: Int): ByteArray = store.groupReferenceTable(indexId, groupId)
    fun validIndexCount() = store.validIndexCount()
    fun index(id: Int): Index = store.index(id)
}
