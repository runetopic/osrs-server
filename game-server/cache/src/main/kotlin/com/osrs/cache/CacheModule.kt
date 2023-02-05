package com.osrs.cache

import com.osrs.cache.entry.config.enum.EnumTypeProvider
import com.osrs.cache.entry.config.location.LocationEntryProvider
import com.osrs.cache.entry.config.varbit.VarBitEntryProvider
import com.osrs.cache.entry.config.varp.VarpEntryProvider
import com.osrs.cache.entry.map.MapSquareTypeProvider
import com.runetopic.cache.store.Js5Store
import dev.misfitlabs.kotlinguice4.KotlinModule

object CacheModule : KotlinModule() {

    override fun configure() {
        bind<Cache>().asEagerSingleton()
        bind<Js5Store>().toProvider<Js5StoreProvider>().asEagerSingleton()
        bind<MapSquareTypeProvider>().asEagerSingleton()
        bind<LocationEntryProvider>().asEagerSingleton()
        bind<EnumTypeProvider>().asEagerSingleton()
        bind<VarBitEntryProvider>().asEagerSingleton()
        bind<VarpEntryProvider>().asEagerSingleton()
    }

    // Indexes.
    const val CONFIG_INDEX = 2
    const val INTERFACE_INDEX = 3
    const val SOUND_EFFECT_INDEX = 4
    const val MAP_INDEX = 5
    const val MUSIC_INDEX = 6
    const val SPRITE_INDEX = 8
    const val TEXTURE_INDEX = 9
    const val BINARY_INDEX = 10
    const val FONT_INDEX = 13
    const val VORBIS_INDEX = 14
    const val INSTRUMENT_INDEX = 15
    const val DB_INDEXES_INDEX = 21

    // Config groups.
    const val FLOOR_UNDERLAY_CONFIG = 1
    const val KIT_CONFIG = 3
    const val FLOOR_OVERLAY_CONFIG = 4
    const val INV_CONFIG = 5
    const val LOC_CONFIG = 6
    const val ENUM_CONFIG = 8
    const val NPC_CONFIG = 9
    const val OBJ_CONFIG = 10
    const val PARAM_CONFIG = 11
    const val SEQUENCE_CONFIG = 12
    const val SPOT_ANIMATION_CONFIG = 13
    const val VARBIT_CONFIG = 14
    const val VARP_CONFIG = 16
    const val VARC_CONFIG = 19
    const val HITSPLAT_CONFIG = 32
    const val HITBAR_CONFIG = 33
    const val STRUCT_CONFIG = 34
    const val WORLD_MAP_CONFIG = 35
    const val DB_ROW_CONFIG = 38
    const val DB_TABLE_CONFIG = 39
}
