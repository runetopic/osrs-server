package com.osrs.cache.entry.config.obj

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule.CONFIG_INDEX
import com.osrs.cache.CacheModule.OBJ_CONFIG
import com.osrs.cache.entry.EntryTypeProvider
import com.osrs.api.buffer.RSByteBuffer

@Singleton
class ObjEntryProvider @Inject constructor(
    val cache: Cache
) : EntryTypeProvider<ObjEntry>() {

    override fun loadTypeMap(): Map<Int, ObjEntry> {
        val objs = cache.index(CONFIG_INDEX)
            .group(OBJ_CONFIG)
            ?.files()
            ?.map { RSByteBuffer(it.data).loadEntryType(ObjEntry(it.id)) }
            ?.associateBy(ObjEntry::id) ?: emptyMap()

        postLoadObjs(objs)
        return objs
    }

    private tailrec fun RSByteBuffer.loadEntryType(type: ObjEntry): ObjEntry {
        when (val opcode = readUByte()) {
            0 -> return type
            1 -> type.model = readUShort()
            2 -> type.name = readStringCp1252NullTerminated()
            4 -> type.zoom2d = readUShort()
            5 -> type.xan2d = readUShort()
            6 -> type.yan2d = readUShort()
            7 -> type.offsetX2d = readUShort().let { if (it > Short.MAX_VALUE) it - 65536 else it }
            8 -> type.offsetY2d = readUShort().let { if (it > Short.MAX_VALUE) it - 65536 else it }
            9 -> readStringCp1252NullTerminated() // Unused.
            11 -> type.isStackable = true
            12 -> type.price = readInt()
            13 -> type.wearPos1 = readUByte()
            14 -> type.wearPos2 = readUByte()
            16 -> type.isMembersOnly = true
            23 -> {
                type.maleModel = readUShort()
                type.maleOffset = readUByte()
            }
            24 -> type.maleModel1 = readUShort()
            25 -> {
                type.femaleModel = readUShort()
                type.femaleOffset = readUByte()
            }
            26 -> type.femaleModel1 = readUShort()
            27 -> type.wearPos3 = readUByte()
            in 30..34 -> type.groundActions = type.groundActions.toMutableList().apply {
                this[opcode - 30] = readStringCp1252NullTerminated().let { if (it.equals("Hidden", true)) "null" else it }
            }
            in 35..39 -> type.inventoryActions = type.inventoryActions.toMutableList().apply {
                this[opcode - 35] = readStringCp1252NullTerminated()
            }
            40 -> {
                val length = readUByte()
                type.colorFind = arrayOfNulls(length)
                type.colorReplace = arrayOfNulls(length)

                for (var4 in 0 until length) {
                    type.colorFind[var4] = readUShort()
                    type.colorReplace[var4] = readUShort()
                }
            }
            41 -> {
                val length = readUByte()
                type.textureFind = arrayOfNulls(length)
                type.textureReplace = arrayOfNulls(length)

                for (var4 in 0 until length) {
                    type.textureFind[var4] = readUShort()
                    type.textureReplace[var4] = readUShort()
                }
            }
            42 -> type.shiftClickIndex = readByte()
            65 -> type.isTradeable = true
            75 -> type.weight = readUShort()
            78 -> type.maleModel2 = readUShort()
            79 -> type.femaleModel2 = readUShort()
            90 -> type.maleHeadModel = readUShort()
            91 -> type.femaleHeadModel = readUShort()
            92 -> type.maleHeadModel2 = readUShort()
            93 -> type.femaleHeadModel2 = readUShort()
            94 -> discard(2) // Unused.
            95 -> type.zan2d = readUShort()
            97 -> type.note = readUShort()
            98 -> type.noteTemplate = readUShort()
            in 100..109 -> discard(4) // Discard countobj/countco.
            110 -> type.resizeX = readUShort()
            111 -> type.resizeY = readUShort()
            112 -> type.resizeZ = readUShort()
            113 -> type.ambient = readByte()
            114 -> type.contrast = readByte() * 5
            115 -> type.team = readUByte()
            139 -> type.unnotedId = readUShort()
            140 -> type.notedId = readUShort()
            148 -> type.placeholder = readUShort()
            149 -> type.placeholderTemplate = readUShort()
            249 -> type.params = readStringIntParameters()
            else -> throw IllegalArgumentException("Missing opcode $opcode.")
        }
        return loadEntryType(type)
    }

    private fun postLoadObjs(objs: Map<Int, ObjEntry>) {
        objs.values.forEach { type ->
            if (type.isStackable) type.weight = 0

            if (type.noteTemplate != -1) {
                objs[type.noteTemplate]?.let { noteTemplate ->
                    objs[type.note]?.let { note ->
                        type.toNote(noteTemplate, note)
                    }
                }
            }

            if (type.notedId != -1) {
                objs[type.notedId]?.let { noted ->
                    objs[type.unnotedId]?.let { unnoted ->
                        type.toUnnoted(noted, unnoted)
                    }
                }
            }

            if (type.placeholderTemplate != -1) {
                objs[type.placeholderTemplate]?.let { placeholderTemplate ->
                    objs[type.placeholder]?.let { placeholder ->
                        type.toPlaceholder(placeholderTemplate, placeholder)
                    }
                }
            }
        }
    }

    private fun ObjEntry.toNote(noteTemplate: ObjEntry, note: ObjEntry) {
        model = noteTemplate.model
        zoom2d = noteTemplate.zoom2d
        xan2d = noteTemplate.xan2d
        yan2d = noteTemplate.yan2d
        zan2d = noteTemplate.zan2d
        offsetX2d = noteTemplate.offsetX2d
        offsetY2d = noteTemplate.offsetY2d
        // Skip recolor.
        // Skip retexture.
        name = note.name
        isMembersOnly = note.isMembersOnly
        price = note.price
        isStackable = true
    }

    private fun ObjEntry.toUnnoted(noted: ObjEntry, unnoted: ObjEntry) {
        model = noted.model
        zoom2d = noted.zoom2d
        xan2d = noted.xan2d
        yan2d = noted.yan2d
        zan2d = noted.zan2d
        offsetX2d = noted.offsetX2d
        offsetY2d = noted.offsetY2d
        // Skip recolor.
        // Skip retexture.
        name = unnoted.name
        isMembersOnly = unnoted.isMembersOnly
        isStackable = unnoted.isStackable
        maleModel = unnoted.maleModel
        maleModel1 = unnoted.maleModel1
        maleModel2 = unnoted.maleModel2
        femaleModel = unnoted.femaleModel
        femaleModel1 = unnoted.femaleModel1
        femaleModel2 = unnoted.femaleModel2
        maleHeadModel = unnoted.maleHeadModel
        maleHeadModel2 = unnoted.maleHeadModel2
        femaleHeadModel = unnoted.femaleHeadModel
        femaleHeadModel2 = unnoted.femaleHeadModel2
        team = unnoted.team
        groundActions = unnoted.groundActions
        inventoryActions = buildList {
            repeat(3) {
                add(unnoted.inventoryActions[it])
            }
            add("Discard")
        }
        price = 0
    }

    private fun ObjEntry.toPlaceholder(placeholderTemplate: ObjEntry, placeholder: ObjEntry) {
        model = placeholderTemplate.model
        zoom2d = placeholderTemplate.zoom2d
        xan2d = placeholderTemplate.xan2d
        yan2d = placeholderTemplate.yan2d
        zan2d = placeholderTemplate.zan2d
        offsetX2d = placeholderTemplate.offsetX2d
        offsetY2d = placeholderTemplate.offsetY2d
        // Skip recolor.
        // Skip retexture.
        isStackable = placeholderTemplate.isStackable
        name = placeholder.name
        price = 0
        isMembersOnly = false
        isTradeable = false
    }
}
