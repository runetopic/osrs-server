package com.osrs.cache.entry.config.enum

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.CacheModule.CONFIG_INDEX
import com.osrs.cache.CacheModule.ENUM_CONFIG
import com.osrs.cache.entry.EntryTypeMapProvider
import com.osrs.cache.entry.config.ScriptType
import com.osrs.common.buffer.readInt
import com.osrs.common.buffer.readStringCp1252NullTerminated
import com.osrs.common.buffer.readUByte
import com.osrs.common.buffer.readUShort
import com.runetopic.cache.store.Js5Store
import java.nio.ByteBuffer

@Singleton
class EnumTypeProvider @Inject constructor(
    private val store: Js5Store
) : EntryTypeMapProvider<EnumEntry>() {
    override fun loadTypeMap(): Map<Int, EnumEntry> {
        return store
            .index(CONFIG_INDEX)
            .group(ENUM_CONFIG)
            .files()
            .map { ByteBuffer.wrap(it.data).loadEntryType(EnumEntry(it.id)) }
            .associateBy(EnumEntry::id)
    }

    private tailrec fun ByteBuffer.loadEntryType(type: EnumEntry): EnumEntry {
        when (val opcode = readUByte()) {
            0 -> { return type }
            1 -> readUByte().toChar().apply {
                type.keyType = enumValues<ScriptType>().find { it.key == this }
            }
            2 -> readUByte().toChar().apply {
                type.valType = enumValues<ScriptType>().find { it.key == this }
            }
            3 -> type.defaultString = readStringCp1252NullTerminated()
            4 -> type.defaultInt = readInt()
            5 -> {
                val size = readUShort()
                type.params = buildMap {
                    repeat(size) {
                        put(readInt(), readStringCp1252NullTerminated())
                    }
                }
                type.size = size
            }
            6 -> {
                val size = readUShort()
                type.params = buildMap {
                    repeat(size) {
                        put(readInt(), readInt())
                    }
                }
                type.size = size
            }
            else -> throw IllegalArgumentException("Missing opcode $opcode.")
        }
        return loadEntryType(type)
    }
}
