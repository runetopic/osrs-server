package com.osrs.cache.config.param

import com.osrs.cache.config.ScriptType
import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.config.ConfigType

class ParamType(id: Int) : ConfigType(id) {
    var type: ScriptType? = null
    var defaultInt: Int = 0
    var autoDisable: Boolean = true
    var defaultString: String = ""

    private val isString get() = this.type == ScriptType.STRING

    override fun read(buf: ByteBuf, code: Int) {
        with (buf) {
            when (code) {
                1 -> readUnsignedByte().toInt().toChar().apply {
                    type = enumValues<ScriptType>().find { it.key == this }
                }
                2 -> defaultInt = readInt()
                4 -> autoDisable = false
                5 -> defaultString = readString()
                else -> error("Missing code $code")
            }
        }
    }

    override fun write(buf: ByteBuf) {
        TODO("Not yet implemented")
    }

    fun default() = if (isString) defaultString else defaultInt
}
