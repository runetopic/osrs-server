package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class ObjAddPacket(
    val id: Int,
    val quantity: Int,
    val packedOffset: Int,
    val disabledOptions: IntArray = intArrayOf(),
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjAddPacket

        if (id != other.id) return false
        if (quantity != other.quantity) return false
        if (packedOffset != other.packedOffset) return false
        if (!disabledOptions.contentEquals(other.disabledOptions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + quantity
        result = 31 * result + packedOffset
        result = 31 * result + disabledOptions.contentHashCode()
        return result
    }
}
