package com.osrs.game.network.packet.type.server

import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.Packet

data class PlayerInfoPacket(
    val viewport: Viewport,
    val players: PlayerList,
    val highDefinitionUpdates: Array<ByteArray?>,
    val lowDefinitionUpdates: Array<ByteArray?>,
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerInfoPacket

        if (viewport != other.viewport) return false
        if (players != other.players) return false
        if (!highDefinitionUpdates.contentDeepEquals(other.highDefinitionUpdates)) return false
        if (!lowDefinitionUpdates.contentDeepEquals(other.lowDefinitionUpdates)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = viewport.hashCode()
        result = 31 * result + players.hashCode()
        result = 31 * result + highDefinitionUpdates.contentDeepHashCode()
        result = 31 * result + lowDefinitionUpdates.contentDeepHashCode()
        return result
    }
}
