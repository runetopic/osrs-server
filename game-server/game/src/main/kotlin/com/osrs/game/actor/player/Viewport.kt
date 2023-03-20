package com.osrs.game.actor.player

import com.osrs.common.buffer.BitAccess
import com.osrs.common.buffer.withBitAccess
import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.npc.NPC
import com.osrs.game.world.World.Companion.MAX_PLAYERS
import java.nio.ByteBuffer

class Viewport(
    val player: Player
) {
    val nsnFlags = IntArray(MAX_PLAYERS)
    val locations = IntArray(MAX_PLAYERS)
    val highDefinitions = IntArray(MAX_PLAYERS)
    val lowDefinitions = IntArray(MAX_PLAYERS)
    val highDefinitionUpdates = ArrayList<Int>()
    val lowDefinitionUpdates = ArrayList<Int>()
    val npcs = ArrayList<NPC>()

    var highDefinitionsCount = 0
    var lowDefinitionsCount = 0
    var forceViewDistance = false
    var viewDistance = PREFERRED_VIEW_DISTANCE

    private var resizeTickCount = 0

    fun init(buffer: ByteBuffer) {
        val bits = BitAccess(buffer)
        bits.writeBits(30, player.location.packed)
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

    fun reset(players: PlayerList) {
        highDefinitionUpdates.clear()
        lowDefinitionUpdates.clear()
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

    private companion object {
        const val RESIZE_CHECK_INTERVAL = 10
        const val PREFERRED_PLAYER_COUNT = 250
        const val PREFERRED_VIEW_DISTANCE = 15
    }
}
