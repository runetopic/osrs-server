package com.osrs.cache.config.param

import com.osrs.cache.Js5Archive
import com.osrs.cache.Js5ConfigGroup
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.openrs2.cache.Cache
import org.openrs2.cache.config.GroupConfigTypeList

@Singleton
class ParamTypeList @Inject constructor(cache: Cache) : GroupConfigTypeList<ParamType>(
    cache,
    archive = Js5Archive.CONFIG_INDEX,
    group = Js5ConfigGroup.PARAM_CONFIG,
) {
    override fun allocate(id: Int): ParamType {
        return ParamType(id)
    }
}
