package com.osrs.game.network.packet.type

import com.osrs.game.network.packet.Packet

data class LocAddPacket(
    val id: Int,
    val shape: Int,
    val rotation: Int,
    val packedOffset: Int,
    val disabledOptions: IntArray = intArrayOf()
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocAddPacket

        if (id != other.id) return false
        if (shape != other.shape) return false
        if (rotation != other.rotation) return false
        if (packedOffset != other.packedOffset) return false
        if (!disabledOptions.contentEquals(other.disabledOptions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + shape
        result = 31 * result + rotation
        result = 31 * result + packedOffset
        result = 31 * result + disabledOptions.contentHashCode()
        return result
    }
}
