package com.osrs.cache.config.obj

import com.osrs.cache.Js5Archive
import com.osrs.cache.Js5ConfigGroup
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.openrs2.cache.Cache
import org.openrs2.cache.config.GroupConfigTypeList

@Singleton
class ObjTypeList @Inject constructor(cache: Cache) : GroupConfigTypeList<ObjType>(
    cache,
    archive = Js5Archive.CONFIG_INDEX,
    group = Js5ConfigGroup.OBJ_CONFIG,
) {
    override fun allocate(id: Int): ObjType {
        return ObjType(id)
    }
}
