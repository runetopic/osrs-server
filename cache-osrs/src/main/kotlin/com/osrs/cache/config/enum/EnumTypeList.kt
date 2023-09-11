package com.osrs.cache.config.enum

import com.osrs.cache.Js5Archive
import com.osrs.cache.Js5ConfigGroup
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.openrs2.cache.Cache
import org.openrs2.cache.config.GroupConfigTypeList

@Singleton
class EnumTypeList @Inject constructor(cache: Cache) : GroupConfigTypeList<EnumType>(
    cache,
    archive = Js5Archive.CONFIG_INDEX,
    group = Js5ConfigGroup.ENUM_CONFIG,
) {
    override fun allocate(id: Int): EnumType {
        return EnumType(id)
    }
}
