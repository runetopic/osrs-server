package com.osrs.game.network.packet.builder.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.common.buffer.BitAccess
import com.osrs.common.buffer.withBitAccess
import com.osrs.common.map.location.Location
import com.osrs.common.map.location.withinDistance
import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.movement.Direction
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.network.packet.type.server.PlayerInfoPacket
import java.nio.ByteBuffer
import kotlin.math.abs

@Singleton
class PlayerInfoPacketBuilder @Inject constructor(
    private val updateBlocks: PlayerUpdateBlocks
) : PacketBuilder<PlayerInfoPacket>(
    opcode = 3,
    size = -2
) {
    override fun build(packet: PlayerInfoPacket, buffer: ByteBuffer) {
        val players = packet.players
        val viewport = packet.viewport
        viewport.resize()
        buffer.syncHighDefinition(viewport, players)
        buffer.syncLowDefinition(viewport, players)
        for (index in viewport.highDefinitionUpdates) {
            buffer.put(updateBlocks.highDefinitionUpdates[index])
        }
        for (index in viewport.lowDefinitionUpdates) {
            buffer.put(updateBlocks.lowDefinitionUpdates[index])
        }
        viewport.reset(players)
    }

    private tailrec fun ByteBuffer.syncHighDefinition(
        viewport: Viewport,
        players: PlayerList,
        nsn: Boolean = true,
        index: Int = 0,
        skip: Int = -1,
        bits: BitAccess = BitAccess(this)
    ) {
        if (index == viewport.highDefinitionsCount) {
            bits.writeSkipCount(skip)
            withBitAccess(bits)
            if (!nsn) return
            return syncHighDefinition(viewport, players, false)
        }
        val playerIndex = viewport.highDefinitions[index]
        if (nsn == (0x1 and viewport.nsnFlags[playerIndex] != 0)) {
            return syncHighDefinition(viewport, players, nsn, index + 1, skip, bits)
        }
        val other = players[playerIndex]
        val removing = viewport.shouldRemove(other, players)
        val moving = other?.moveDirection != MoveDirection.None
        val updating = other?.let { updateBlocks.highDefinitionUpdates[it.index] } != null
        val active = removing || updating || moving
        if (other == null || !active) {
            viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
            return syncHighDefinition(viewport, players, nsn, index + 1, skip + 1, bits)
        }
        val offset = bits.writeSkipCount(skip)
        bits.writeBit(true)
        bits.processHighDefinitionPlayer(viewport, other, playerIndex, removing, moving, updating)
        return syncHighDefinition(viewport, players, nsn, index + 1, offset, bits)
    }

    private tailrec fun ByteBuffer.syncLowDefinition(
        viewport: Viewport,
        players: PlayerList,
        nsn: Boolean = true,
        index: Int = 0,
        skip: Int = -1,
        bits: BitAccess = BitAccess(this)
    ) {
        if (index == viewport.lowDefinitionsCount) {
            bits.writeSkipCount(skip)
            withBitAccess(bits)
            if (!nsn) return
            return syncLowDefinition(viewport, players, false)
        }
        val playerIndex = viewport.lowDefinitions[index]
        if (nsn == (0x1 and viewport.nsnFlags[playerIndex] == 0)) {
            return syncLowDefinition(viewport, players, nsn, index + 1, skip, bits)
        }
        val other = players[playerIndex]
        val adding = viewport.shouldAdd(other)
        val updating = other?.let { updateBlocks.lowDefinitionUpdates[it.index] } != null
        val active = adding || updating
        if (other == null || !active) {
            viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
            return syncLowDefinition(viewport, players, nsn, index + 1, skip + 1, bits)
        }
        val offset = bits.writeSkipCount(skip)
        bits.writeBit(true)
        bits.processLowDefinitionPlayer(viewport, other, playerIndex)
        return syncLowDefinition(viewport, players, nsn, index + 1, offset, bits)
    }

    private fun BitAccess.processHighDefinitionPlayer(
        viewport: Viewport,
        other: Player,
        index: Int,
        removing: Boolean,
        moving: Boolean,
        updating: Boolean
    ) {
        writeBit(if (removing) false else updating)
        when {
            removing -> { // remove the player
                // send a position update
                writeBits(2, 0)
                viewport.locations[index] = other.location.regionLocation
                validateLocationChanges(viewport, other, index)
            }
            moving -> {
                val moveDirection = other.moveDirection
                var dx = Direction.DIRECTION_DELTA_X[moveDirection.walkDirection!!.opcode]
                var dz = Direction.DIRECTION_DELTA_Z[moveDirection.walkDirection.opcode]
                var running = other.moveDirection.runDirection != null
                var direction = 0

                if (running) {
                    dx += Direction.DIRECTION_DELTA_X[moveDirection.runDirection!!.opcode]
                    dz += Direction.DIRECTION_DELTA_Z[moveDirection.runDirection.opcode]
                    direction = Direction.getPlayerRunningDirection(dx, dz)
                    running = direction != -1
                }

                if (!running) {
                    direction = Direction.getPlayerWalkingDirection(dx, dz)
                }

                writeBits(2, if (running) 2 else 1) // 2 for running
                writeBits(if (running) 4 else 3, direction) // Opcode for direction bit 3 for walking bit 4 for running.
                viewport.locations[index] = other.location.regionLocation
            }
            updating -> {
                // send a block update
                writeBits(2, 0)
            }
        }
        if (!removing && updating) {
            viewport.highDefinitionUpdates += other.index
        }
    }

    private fun BitAccess.processLowDefinitionPlayer(
        viewport: Viewport,
        other: Player,
        index: Int
    ) {
        writeBits(2, 0)
        validateLocationChanges(viewport, other, index)
        writeBits(13, other.location.x)
        writeBits(13, other.location.z)
        writeBit(true)
        viewport.nsnFlags[index] = viewport.nsnFlags[index] or 2
        viewport.lowDefinitionUpdates += other.index
    }

    private fun BitAccess.validateLocationChanges(viewport: Viewport, other: Player, index: Int) {
        val currentPacked = viewport.locations[index]
        val packed = other.location.regionLocation
        val updating = packed != currentPacked
        writeBit(updating)
        if (updating) {
            updateCoordinates(currentPacked, packed)
            viewport.locations[index] = packed
        }
    }

    private fun BitAccess.updateCoordinates(lastCoordinates: Int, currentCoordinates: Int) {
        val lastPlane = lastCoordinates shr 16
        val lastRegionX = lastCoordinates shr 8
        val lastRegionZ = lastCoordinates and 0xff

        val currentPlane = currentCoordinates shr 16
        val currentRegionX = currentCoordinates shr 8
        val currentRegionZ = currentCoordinates and 0xff

        val deltaPlane = currentPlane - lastPlane
        val deltaX = currentRegionX - lastRegionX
        val deltaZ = currentRegionZ - lastRegionZ

        if (lastRegionX == currentRegionX && lastRegionZ == currentRegionZ) {
            writeBits(2, 1)
            writeBits(2, deltaPlane)
        } else if (abs(currentRegionX - lastRegionX) <= 1 && abs(currentRegionZ - lastRegionZ) <= 1) {
            // TODO Extract this directional stuff out.
            val opcode = when {
                deltaX == -1 && deltaZ == -1 -> 0
                deltaX == 1 && deltaZ == -1 -> 2
                deltaX == -1 && deltaZ == 1 -> 5
                deltaX == 1 && deltaZ == 1 -> 7
                deltaZ == -1 -> 1
                deltaX == -1 -> 3
                deltaX == 1 -> 4
                else -> 6
            }
            writeBits(2, 2)
            writeBits(5, (deltaPlane shl 3) + (opcode and 0x7))
        } else {
            writeBits(2, 3)
            writeBits(18, (deltaZ and 0xff) + (deltaX and 0xff shl 8) + (deltaPlane shl 16))
        }
    }

    private fun BitAccess.writeSkipCount(count: Int): Int {
        if (count == -1) return count
        writeBit(false)
        when {
            count == 0 -> writeBits(2, 0)
            count < 32 -> {
                writeBits(2, 1)
                writeBits(5, count)
            }
            count < 256 -> {
                writeBits(2, 2)
                writeBits(8, count)
            }
            count < 2048 -> {
                writeBits(2, 3)
                writeBits(11, count)
            }
        }
        return -1
    }

    private fun Viewport.shouldAdd(other: Player?): Boolean = when {
        other == null -> false
        other == player -> false
        other.location == Location.None -> false
        !other.location.withinDistance(player.location) -> false
        else -> true
    }

    private fun Viewport.shouldRemove(other: Player?, players: PlayerList): Boolean = when {
        other == null -> true
        !other.online -> true
        other.location == Location.None -> true
        !other.location.withinDistance(player.location) -> true
        !players.contains(other) -> true
        else -> false
    }
}
