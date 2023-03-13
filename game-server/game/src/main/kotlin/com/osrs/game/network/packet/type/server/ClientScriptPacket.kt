package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet

data class ClientScriptPacket(
    val id: Int,
    val parameters: Array<out Any>,
) : Packet {
    constructor(id: Int) : this(id, emptyArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientScriptPacket

        if (id != other.id) return false
        if (!parameters.contentEquals(other.parameters)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + parameters.contentHashCode()
        return result
    }
}
