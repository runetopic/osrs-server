package com.osrs.game.actor.player

import com.osrs.common.buffer.BitAccess
import com.osrs.common.buffer.withBitAccess
import com.osrs.game.world.World.Companion.MAX_PLAYERS
import java.nio.ByteBuffer

class Viewport(
    val player: Player
) {
    val nsnFlags = IntArray(MAX_PLAYERS)
    val locations = IntArray(MAX_PLAYERS)
    val highDefinitions = IntArray(MAX_PLAYERS)
    val lowDefinitions = IntArray(MAX_PLAYERS)
    val players = arrayOfNulls<Player?>(MAX_PLAYERS)
    var highDefinitionsCount = 0
    var lowDefinitionsCount = 0
    var forceViewDistance = false
    var viewDistance = PREFERRED_VIEW_DISTANCE

    private var resizeTickCount = 0

    fun init(buffer: ByteBuffer) {
        val bits = BitAccess(buffer)
        bits.writeBits(30, player.location.packedLocation)
        players[player.index] = player
        highDefinitions[highDefinitionsCount++] = player.index
        for (index in 1 until MAX_PLAYERS) {
            if (index == player.index) continue
            val otherRegionCoordinates = player.world.players[index]?.location?.regionLocation ?: 0
            bits.writeBits(18, otherRegionCoordinates)
            locations[index] = otherRegionCoordinates
            lowDefinitions[lowDefinitionsCount++] = index
        }
        buffer.withBitAccess(bits)
    }

    fun reset() {
        highDefinitionsCount = 0
        lowDefinitionsCount = 0
        for (index in 1 until MAX_PLAYERS) {
            if (players[index] == null) {
                lowDefinitions[lowDefinitionsCount++] = index
            } else {
                highDefinitions[highDefinitionsCount++] = index
            }
            nsnFlags[index] = (nsnFlags[index] shr 1)
        }
    }

    fun resize() {
        // Thank you Kris =)
        if (forceViewDistance) return
        if (highDefinitionsCount >= PREFERRED_PLAYER_COUNT) {
            if (viewDistance > 0) viewDistance--
            resizeTickCount = 0
            return
        }
        if (++resizeTickCount >= RESIZE_CHECK_INTERVAL) {
            if (viewDistance < PREFERRED_VIEW_DISTANCE) {
                viewDistance++
            } else {
                resizeTickCount = 0
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Viewport

        if (!nsnFlags.contentEquals(other.nsnFlags)) return false
        if (!locations.contentEquals(other.locations)) return false
        if (!highDefinitions.contentEquals(other.highDefinitions)) return false
        if (!lowDefinitions.contentEquals(other.lowDefinitions)) return false
        if (!players.contentEquals(other.players)) return false
        if (highDefinitionsCount != other.highDefinitionsCount) return false
        if (lowDefinitionsCount != other.lowDefinitionsCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nsnFlags.contentHashCode()
        result = 31 * result + locations.contentHashCode()
        result = 31 * result + highDefinitions.contentHashCode()
        result = 31 * result + lowDefinitions.contentHashCode()
        result = 31 * result + players.contentHashCode()
        result = 31 * result + highDefinitionsCount
        result = 31 * result + lowDefinitionsCount
        return result
    }

    companion object {
        const val RESIZE_CHECK_INTERVAL = 10
        const val PREFERRED_PLAYER_COUNT = 250
        const val PREFERRED_VIEW_DISTANCE = 15
    }
}
