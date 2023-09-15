package com.osrs.cache.client

import com.google.inject.Inject
import com.google.inject.Provider
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.MasterIndexFormat
import org.openrs2.cache.Store

class Js5MasterIndexProvider @Inject constructor(
    @ClientCache
    private val store: Store
) : Provider<Js5MasterIndex> {
    override fun get(): Js5MasterIndex {
        val masterIndex = Js5MasterIndex.create(store)
        masterIndex.format = MasterIndexFormat.VERSIONED
        return masterIndex
    }
}
