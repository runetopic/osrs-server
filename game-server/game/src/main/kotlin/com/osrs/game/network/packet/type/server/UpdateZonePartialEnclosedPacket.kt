package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class UpdateZonePartialEnclosedPacket(
    val xInScene: Int,
    val zInScene: Int,
    val bytes: ByteArray
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateZonePartialEnclosedPacket

        if (xInScene != other.xInScene) return false
        if (zInScene != other.zInScene) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = xInScene
        result = 31 * result + zInScene
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}
