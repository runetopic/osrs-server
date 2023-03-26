package com.osrs.game.actor.player

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.npc.NPC
import com.osrs.game.world.World.Companion.MAX_PLAYERS

class Viewport(
    val player: Player
) {
    val nsnFlags = IntArray(MAX_PLAYERS)
    val locations = IntArray(MAX_PLAYERS)
    val highDefinitions = IntArray(MAX_PLAYERS)
    val lowDefinitions = IntArray(MAX_PLAYERS)
    val highDefinitionUpdates = ArrayList<Int>()
    val lowDefinitionUpdates = ArrayList<Int>()
    val players = arrayOfNulls<Player?>(MAX_PLAYERS)
    val npcs = arrayOfNulls<NPC?>(250)

    var highDefinitionsCount = 0
        private set
    var lowDefinitionsCount = 0
        private set
    var forceViewDistance = false
        private set
    var viewDistance = PREFERRED_VIEW_DISTANCE
        private set

    private var resizeTickCount = 0

    fun init(buffer: RSByteBuffer) {
        buffer.accessBits()
        buffer.writeBits(30, player.location.packed)
        players[player.index] = player
        highDefinitions[highDefinitionsCount++] = player.index
        for (index in 1 until MAX_PLAYERS) {
            if (index == player.index) continue
            val otherRegionCoordinates = player.world.players[index]?.location?.regionLocation ?: 0
            buffer.writeBits(18, otherRegionCoordinates)
            locations[index] = otherRegionCoordinates
            lowDefinitions[lowDefinitionsCount++] = index
        }
        buffer.accessBytes()
    }

    fun reset() {
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
