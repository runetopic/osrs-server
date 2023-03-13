package com.osrs.cache.entry.config.varp

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule.CONFIG_INDEX
import com.osrs.cache.CacheModule.VARP_CONFIG
import com.osrs.cache.entry.EntryTypeProvider
import com.osrs.common.buffer.readUByte
import com.osrs.common.buffer.readUShort
import java.nio.ByteBuffer

@Singleton
class VarpEntryProvider @Inject constructor(
    val cache: Cache,
) : EntryTypeProvider<VarpEntry>() {
    override fun loadTypeMap(): Map<Int, VarpEntry> = cache.index(CONFIG_INDEX)
        .group(VARP_CONFIG)
        ?.files()
        ?.map { ByteBuffer.wrap(it.data).loadEntryType(VarpEntry(it.id)) }
        ?.associateBy(VarpEntry::id) ?: emptyMap()

    private tailrec fun ByteBuffer.loadEntryType(type: VarpEntry): VarpEntry {
        when (val opcode = readUByte()) {
            0 -> { return type }
            5 -> type.type = readUShort()
            else -> throw IllegalArgumentException("Missing opcode $opcode.")
        }
        return loadEntryType(type)
    }
}
