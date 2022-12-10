package com.osrs.game.network.packet.server

import com.osrs.game.network.packet.Packet

data class PlayerInfoPacket(
    val buffer: ByteArray
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerInfoPacket

        if (!buffer.contentEquals(other.buffer)) return false

        return true
    }

    override fun hashCode(): Int {
        return buffer.contentHashCode()
    }
}
