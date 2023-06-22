package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.api.item.Item
import com.osrs.game.container.Container
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.ClientScriptPacket

@Singleton
class RunClientScriptPacketBuilder : PacketBuilder<ClientScriptPacket>(
    opcode = 18,
    size = -2
) {
    private fun mapParameterType(it: Array<out Any>, count: Int) = when (it[count]) {
        is String -> "s"
        is Item -> "o"
        is Container -> "v"
        is Int -> "i"
        else -> throw IllegalStateException("Run client script type was not found during parameter type mapping. The found type was ${it[count]}")
    }

    override fun build(packet: ClientScriptPacket, buffer: RSByteBuffer) {
        packet.parameters.let { array ->
            val params = buildString {
                array.indices.forEach { count ->
                    append(mapParameterType(array, count))
                }
            }
            buffer.writeStringCp1252NullTerminated(params)
            var index = array.size - 1

            array.indices.forEach { count ->
                when (params[count]) {
                    's' -> buffer.writeStringCp1252NullTerminated(array[index--] as String) // String
                    'o' -> buffer.writeInt((array[index--] as Item).id) // Item
                    'i' -> buffer.writeInt(array[index--] as Int) // Int
                    'v' -> buffer.writeInt((array[index--] as Container).id)
                    else -> throw IllegalStateException("Run client script type was not found during parameter type write. The found type was ${params[count]}")
                }
            }
        }
        buffer.writeInt(packet.id)
    }
}
