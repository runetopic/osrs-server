package com.osrs.cache

import com.google.inject.Inject
import org.openrs2.cache.Store

class Cache @Inject constructor(
    private val store: Store
) {
    val checksums = byteArrayOf()
    val crcs = intArrayOf()

    fun groupReferenceTable(indexId: Int, groupId: Int): ByteArray = byteArrayOf()
    fun validIndexCount() = 0
    fun index(id: Int) {}
}
