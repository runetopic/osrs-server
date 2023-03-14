package com.osrs.cache.entry.config.varp

import com.osrs.cache.entry.EntryType

data class VarpEntry(
    override val id: Int,
    var type: Int = 0
) : EntryType(id)
