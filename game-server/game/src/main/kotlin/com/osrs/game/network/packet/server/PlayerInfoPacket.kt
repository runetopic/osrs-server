package com.osrs.game.network.packet.server

import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.Packet

data class PlayerInfoPacket(
    val viewport: Viewport,
    val players: PlayerList,
    val playerBlockUpdates: Array<ByteArray?>
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerInfoPacket

        if (viewport != other.viewport) return false
        if (players != other.players) return false
        if (!playerBlockUpdates.contentDeepEquals(other.playerBlockUpdates)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = viewport.hashCode()
        result = 31 * result + players.hashCode()
        result = 31 * result + playerBlockUpdates.contentDeepHashCode()
        return result
    }
}
