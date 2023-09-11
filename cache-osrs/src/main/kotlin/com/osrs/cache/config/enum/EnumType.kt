package com.osrs.cache.config.enum

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.buffer.readString
import org.openrs2.cache.config.ConfigType

class EnumType(id: Int) : ConfigType(id) {
    var keyType: ScriptType? = null
    var valueType: ScriptType? = null
    var defaultString: String = "null"
    var defaultInt: Int = 0

    // TODO(gpe): methods for manipulating the maps
    private var strings: Int2ObjectMap<String>? = null
    private var ints: Int2IntMap? = null

    override fun read(buf: ByteBuf, code: Int) {
        when (code) {
            1 -> buf.readByte().toInt().toChar().apply {
                keyType = enumValues<ScriptType>().find { it.key == this }
            }
            2 -> buf.readByte().toInt().toChar().apply {
                keyType = enumValues<ScriptType>().find { it.key == this }
            }
            3 -> defaultString = buf.readString()
            4 -> defaultInt = buf.readInt()
            5 -> {
                val size = buf.readUnsignedShort()
                val strings = Int2ObjectOpenHashMap<String>()

                for (i in 0 until size) {
                    val key = buf.readInt()
                    strings[key] = buf.readString()
                }

                this.strings = strings
            }

            6 -> {
                val size = buf.readUnsignedShort()
                val ints = Int2IntOpenHashMap()

                for (i in 0 until size) {
                    val key = buf.readInt()
                    ints[key] = buf.readInt()
                }

                this.ints = ints
            }

            else -> throw IllegalArgumentException("Unsupported config code: $code")
        }
    }

    override fun write(buf: ByteBuf) {
        TODO()
    }

    fun getString(key: Int): String {
        val strings = strings ?: return defaultString
        return strings.getOrDefault(key, defaultString)
    }

    fun getInt(key: Int): Int {
        val ints = ints ?: return defaultInt
        return ints.getOrDefault(key, defaultInt)
    }
}
