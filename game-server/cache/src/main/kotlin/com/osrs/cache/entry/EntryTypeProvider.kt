package com.osrs.cache.entry

interface EntryTypeProvider<T, R> {
    fun loadEntryTypeMap(): Map<T, R>
}
