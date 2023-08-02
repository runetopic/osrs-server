package com.osrs.cache.entry

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class EntryType(
    @Transient open val id: Int = -1
)
