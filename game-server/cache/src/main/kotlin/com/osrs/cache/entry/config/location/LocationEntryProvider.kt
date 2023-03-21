package com.osrs.cache.entry.config.location

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule.CONFIG_INDEX
import com.osrs.cache.CacheModule.LOC_CONFIG
import com.osrs.cache.entry.EntryTypeProvider
import com.osrs.common.buffer.RSByteBuffer

@Singleton
class LocationEntryProvider @Inject constructor(
    private val cache: Cache
) : EntryTypeProvider<LocationEntry>() {

    override fun loadTypeMap(): Map<Int, LocationEntry> = cache
        .index(CONFIG_INDEX)
        .group(LOC_CONFIG)
        ?.files()
        ?.map { RSByteBuffer(it.data).loadEntryType(LocationEntry(it.id)) }
        ?.associateBy(LocationEntry::id) ?: emptyMap()

    private tailrec fun RSByteBuffer.loadEntryType(type: LocationEntry): LocationEntry {
        when (val opcode = readUByte()) {
            0 -> { return type }
            1 -> repeat(readUByte()) {
                discard(3) // Discard models/modelIds.
            }
            2 -> type.name = readStringCp1252NullTerminated()
            5 -> repeat(readUByte()) {
                discard(2) // Discard models/modelIds.
            }
            14 -> type.width = readUByte()
            15 -> type.height = readUByte()
            17 -> {
                type.interactType = 0
                type.blockProjectile = false
            }
            18 -> type.blockProjectile = false
            19 -> type.int1 = readUByte()
            21 -> type.clipType = 0
            22 -> type.nonFlatShading = true
            23 -> type.modelClipped = true
            24 -> type.animationId = readUShort().let { if (it == 0xffff) -1 else it }
            27 -> type.interactType = 1
            28 -> type.int2 = readUByte()
            29 -> type.ambient = readByte()
            39 -> type.contrast = readByte() * 25
            in 30..34 -> type.actions = type.actions.toMutableList().apply {
                this[opcode - 30] = readStringCp1252NullTerminated().let { if (it.equals("Hidden", true)) "null" else it }
            }
            40 -> repeat(readUByte()) {
                discard(4) // Discard recolor.
            }
            41 -> repeat(readUByte()) {
                discard(4) // Discard retexture.
            }
            61 -> discard(2) // Unused.
            62 -> type.isRotated = true
            64 -> type.clipped = false
            65 -> type.modelSizeX = readUShort()
            66 -> type.modelHeight = readUShort()
            67 -> type.modelSizeY = readUShort()
            68 -> type.mapSceneId = readUShort()
            69 -> discard(1) // Unused.
            70 -> type.offsetX = readShort()
            71 -> type.offsetHeight = readShort()
            72 -> type.offsetY = readShort()
            73 -> type.boolean2 = true
            74 -> type.breakRouteFinding = true
            75 -> type.int3 = readUByte()
            77, 92 -> {
                type.transformVarbit = readUShort().let { if (it == 0xffff) -1 else it }
                type.transformVarp = readUShort().let { if (it == 0xffff) -1 else it }
                val prime = if (opcode == 77) -1 else readUShort().let { if (it == 0xffff) -1 else it }
                type.transforms = buildList {
                    repeat(readUByte() + 1) {
                        add(readUShort().let { if (it == 0xffff) -1 else it })
                    }
                    add(prime)
                }
            }
            78 -> {
                type.ambientSoundId = readUShort()
                type.int7 = readUByte()
            }
            79 -> {
                type.int5 = readUShort()
                type.int6 = readUShort()
                type.int7 = readUByte()
                repeat(readUByte()) {
                    discard(2) // Discard sound effect ids.
                }
            }
            81 -> type.clipType = readUByte() * 256
            82 -> type.mapIconId = readUShort()
            89 -> type.boolean3 = false
            249 -> type.params = buildMap {
                repeat(readUByte()) {
                    val usingString = readUByte() == 1
                    put(readU24BitInt(), if (usingString) readStringCp1252NullTerminated() else readInt())
                }
            }
            else -> throw IllegalArgumentException("Missing opcode $opcode.")
        }
        return loadEntryType(type)
    }
}
