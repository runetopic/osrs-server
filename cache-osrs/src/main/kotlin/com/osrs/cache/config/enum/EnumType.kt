package com.osrs.cache.config.enum

import com.github.michaelbull.logging.InlineLogger
import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.openrs2.buffer.readString
import org.openrs2.buffer.writeString
import org.openrs2.cache.config.ConfigType

class EnumType(id: Int) : ConfigType(id) {
    var keyType: Char = 0.toChar()
    var valueType: Char = 0.toChar()
    var defaultString: String = "null"
    var defaultInt: Int = 0

    // TODO(gpe): methods for manipulating the maps
    private var strings: Int2ObjectMap<String>? = null
    private var ints: Int2IntMap? = null


    val logger = InlineLogger()

    override fun read(buf: ByteBuf, code: Int) {
        when (code) {
            1 -> keyType = buf.readByte().toInt().toChar()
            2 -> valueType = buf.readByte().toInt().toChar()
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
        if (keyType.code != 0) {
            buf.writeByte(1)
            buf.writeByte(keyType.code)
        }

        if (valueType.code != 0) {
            buf.writeByte(2)
            buf.writeByte(valueType.code)
        }

        if (defaultString != "null") {
            buf.writeByte(3)
            buf.writeString(defaultString)
        }

        if (defaultInt != 0) {
            buf.writeByte(4)
            buf.writeInt(defaultInt)
        }

        val strings = strings
        if (strings != null && strings.isNotEmpty()) {
            buf.writeByte(5)
            buf.writeShort(strings.size)

            for ((key, value) in strings.int2ObjectEntrySet()) {
                buf.writeInt(key)
                buf.writeString(value)
            }
        }

        val ints = ints
        if (ints != null && ints.isNotEmpty()) {
            buf.writeByte(6)
            buf.writeShort(ints.size)

            for ((key, value) in ints.int2IntEntrySet()) {
                buf.writeInt(key)
                buf.writeInt(value)
            }
        }

        buf.writeByte(0)
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
