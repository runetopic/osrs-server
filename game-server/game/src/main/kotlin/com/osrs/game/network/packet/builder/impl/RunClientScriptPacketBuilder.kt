package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.ClientScriptPacket

@Singleton
class RunClientScriptPacketBuilder : PacketBuilder<ClientScriptPacket>(
    opcode = 18,
    size = -2
) {

    override fun build(packet: ClientScriptPacket, buffer: RSByteBuffer) {
        buffer.writeStringCp1252NullTerminated(mapTypes(packet))

        packet.parameters.reversed().forEach { param ->
            when (param) {
                is String -> buffer.writeStringCp1252NullTerminated(param)
                else -> buffer.writeInt(param as Int)
            }
        }

        buffer.writeInt(packet.id)
    }

    private fun mapTypes(packet: ClientScriptPacket) = buildString {
        packet.parameters.forEach { type ->
            when (type) {
                is String -> append('s')
                is Int -> append('i')
                else -> throw IllegalStateException("Unhandled Cs2 type $type")
            }
        }
    }
}
