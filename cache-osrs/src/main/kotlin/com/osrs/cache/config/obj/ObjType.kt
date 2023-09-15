package com.osrs.cache.config.obj

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.buffer.readString
import org.openrs2.cache.config.ConfigType

class ObjType(id: Int) : ConfigType(id) {
    var model: Int = 0
    var name: String = "null"
    var desc: String? = null
    var category: Int = -1
    var zoom2d: Int = 2000
    var xan2d: Int = 0
    var yan2d: Int = 0
    var zan2d: Int = 0
    var weight: Int = 0
    var offsetX2d: Int = 0
    var offsetY2d: Int = 0
    var stackable: Boolean = false
    var cost: Int = 1
    var wearPos1: Int = -1
    var wearPos2: Int = -1
    var wearPos3: Int = -1
    var code9: String = "null"
    var code10: Int = -1
    var colorFind: Array<Int?> = emptyArray()
    var colorReplace: Array<Int?> = emptyArray()
    var textureFind: Array<Int?> = emptyArray()
    var textureReplace: Array<Int?> = emptyArray()
    var isMembersOnly: Boolean = false
    var ops: Array<String?> = Array(5) { null }
    var iops: Array<String?> = Array(5) { null }
    var shiftClickIndex: Int = -2
    var maleModel: Int = -1
    var maleModel1: Int = -1
    var maleOffsetY: Int = 0
    var femaleModel: Int = -1
    var femaleModel1: Int = -1
    var femaleOffsetY: Int = 0
    var maleModel2: Int = -1
    var femaleModel2: Int = -1
    var maleHeadModel: Int = -1
    var maleHeadModel2: Int = -1
    var femaleHeadModel: Int = -1
    var femaleHeadModel2: Int = -1
    var note: Int = -1
    var noteTemplate: Int = -1
    var resizeX: Int = 128
    var resizeY: Int = 128
    var resizeZ: Int = 128
    var ambient: Int = 0
    var contrast: Int = 0
    var team: Int = 0
    var isTradeable: Boolean = false
    var unnotedId: Int = -1
    var notedId: Int = -1
    var placeholder: Int = -1
    var placeholderTemplate: Int = -1
    val params: Int2ObjectMap<Any> = Int2ObjectOpenHashMap()

    override fun read(buf: ByteBuf, code: Int) {
        with(buf) {
            when (code) {
                1 -> model = readUnsignedShort()
                2 -> name = readString()
                3 -> desc = readString()
                4 -> zoom2d = readUnsignedShort()
                5 -> xan2d = readUnsignedShort()
                6 -> yan2d = readUnsignedShort()
                7 -> offsetX2d = readUnsignedShort().let { if (it > Short.MAX_VALUE) it - 65536 else it }
                8 -> offsetY2d = readUnsignedShort().let { if (it > Short.MAX_VALUE) it - 65536 else it }
                9 -> code9 = readString() // Unused.
                10 -> code10 = readUnsignedShort()
                11 -> stackable = true
                12 -> cost = readInt()
                13 -> wearPos1 = readUnsignedByte().toInt()
                14 -> wearPos2 = readUnsignedByte().toInt()
                16 -> isMembersOnly = true
                23 -> {
                    maleModel = readUnsignedShort()
                    maleOffsetY = readUnsignedByte().toInt()
                }

                24 -> maleModel1 = readUnsignedShort()
                25 -> {
                    femaleModel = readUnsignedShort()
                    femaleOffsetY = readUnsignedByte().toInt()
                }

                26 -> femaleModel1 = readUnsignedShort()
                27 -> wearPos3 = readUnsignedByte().toInt()
                in 30..34 -> ops[code - 30] = readString()
                in 35..39 -> iops[code - 35] = readString()
                40 -> {
                    val length = readUnsignedByte().toInt()
                    colorFind = arrayOfNulls(length)
                    colorReplace = arrayOfNulls(length)

                    for (var4 in 0 until length) {
                        colorFind[var4] = readUnsignedShort()
                        colorReplace[var4] = readUnsignedShort()
                    }
                }

                41 -> {
                    val length = readUnsignedByte().toInt()
                    textureFind = arrayOfNulls(length)
                    textureReplace = arrayOfNulls(length)

                    for (var4 in 0 until length) {
                        textureFind[var4] = readUnsignedShort()
                        textureReplace[var4] = readUnsignedShort()
                    }
                }

                42 -> shiftClickIndex = readByte().toInt()
                65 -> isTradeable = true
                75 -> weight = readUnsignedShort()
                78 -> maleModel2 = readUnsignedShort()
                79 -> femaleModel2 = readUnsignedShort()
                90 -> maleHeadModel = readUnsignedShort()
                91 -> femaleHeadModel = readUnsignedShort()
                92 -> maleHeadModel2 = readUnsignedShort()
                93 -> femaleHeadModel2 = readUnsignedShort()
                94 -> category = readUnsignedShort()
                95 -> zan2d = readUnsignedShort()
                97 -> note = readUnsignedShort()
                98 -> noteTemplate = readUnsignedShort()
                in 100..109 -> skipBytes(4) // Discard countobj/countco.
                110 -> resizeX = readUnsignedShort()
                111 -> resizeY = readUnsignedShort()
                112 -> resizeZ = readUnsignedShort()
                113 -> ambient = readByte().toInt()
                114 -> contrast = readByte() * 5
                115 -> team = readUnsignedByte().toInt()
                139 -> unnotedId = readUnsignedShort()
                140 -> notedId = readUnsignedShort()
                148 -> placeholder = readUnsignedShort()
                149 -> placeholderTemplate = readUnsignedShort()
                249 -> {
                    val size = buf.readUnsignedByte().toInt()
                    for (i in 0 until size) {
                        val string = buf.readBoolean()
                        val id = buf.readUnsignedMedium()

                        if (string) {
                            params[id] = buf.readString()
                        } else {
                            params[id] = buf.readInt()
                        }
                    }
                }

                else -> throw IllegalArgumentException("Unsupported config code: $code")
            }
        }
    }

    override fun write(buf: ByteBuf) {
        TODO("Not yet implemented")
    }
}
