package com.osrs.cache.entry.config.enum

import com.osrs.cache.entry.EntryType
import com.osrs.cache.entry.config.ScriptType

data class EnumEntry(
    override val id: Int,
    var keyType: ScriptType? = null,
    var valType: ScriptType? = null,
    var defaultString: String = "null",
    var defaultInt: Int = 0,
    var size: Int = 0,
    var params: Map<Int, Any> = mapOf()
) : EntryType(
    id
)
