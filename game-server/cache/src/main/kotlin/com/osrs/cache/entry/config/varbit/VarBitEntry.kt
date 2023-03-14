package com.osrs.cache.entry.config.varbit

import com.osrs.cache.entry.EntryType

data class VarBitEntry(
    override val id: Int,
    var index: Int = -1,
    var leastSignificantBit: Int = -1,
    var mostSignificantBit: Int = -1,
) : EntryType(id)
