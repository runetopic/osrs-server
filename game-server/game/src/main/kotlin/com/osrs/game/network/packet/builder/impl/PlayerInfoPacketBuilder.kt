package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.BitAccess
import com.osrs.common.buffer.withBitAccess
import com.osrs.common.map.location.withinDistance
import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.movement.Direction
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.PlayerInfoPacket
import java.nio.ByteBuffer
import kotlin.math.abs

@Singleton
class PlayerInfoPacketBuilder : PacketBuilder<PlayerInfoPacket>(
    opcode = 3,
    size = -2
) {
    override fun build(packet: PlayerInfoPacket, buffer: ByteBuffer) {
        packet.viewport.resize()

        buffer.syncHighDefinition(packet.viewport, packet.highDefinitionUpdates)
        buffer.syncLowDefinition(packet.viewport, packet.lowDefinitionUpdates, packet.players)

        for (update in packet.viewport.updates) {
            if (update == null) continue
            buffer.put(update)
        }

        packet.viewport.reset()
    }

    private tailrec fun ByteBuffer.syncHighDefinition(
        viewport: Viewport,
        updates: Array<ByteArray?>,
        nsn: Boolean = true,
        index: Int = 0,
        skip: Int = -1,
        bits: BitAccess = BitAccess(this)
    ) {
        if (index == viewport.highDefinitionsCount) {
            bits.writeSkipCount(skip)
            withBitAccess(bits)
            if (nsn) return syncHighDefinition(viewport, updates, false)
            return
        }
        val playerIndex = viewport.highDefinitions[index]
        if (nsn == (0x1 and viewport.nsnFlags[playerIndex] != 0)) {
            return syncHighDefinition(viewport, updates, nsn, index + 1, skip, bits)
        }
        val other = viewport.players[playerIndex]
        val removing = viewport.shouldRemove(other)
        val pendingUpdates = other?.let { updates[it.index] }
        val updating = pendingUpdates != null || other?.moveDirection != null
        val active = removing || updating
        if (other == null || !active) {
            viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
            return syncHighDefinition(viewport, updates, nsn, index + 1, skip + 1, bits)
        }
        val offset = bits.writeSkipCount(skip)
        bits.writeBit(true)
        bits.processHighDefinitionPlayer(viewport, other, playerIndex, removing, pendingUpdates)
        return syncHighDefinition(viewport, updates, nsn, index + 1, offset, bits)
    }

    private tailrec fun ByteBuffer.syncLowDefinition(
        viewport: Viewport,
        updates: Array<ByteArray?>,
        players: PlayerList,
        nsn: Boolean = true,
        index: Int = 0,
        skip: Int = -1,
        bits: BitAccess = BitAccess(this)
    ) {
        if (index == viewport.lowDefinitionsCount) {
            bits.writeSkipCount(skip)
            withBitAccess(bits)
            if (nsn) return syncLowDefinition(viewport, updates, players, false)
            return
        }
        val playerIndex = viewport.lowDefinitions[index]
        if (nsn == (0x1 and viewport.nsnFlags[playerIndex] == 0)) {
            return syncLowDefinition(viewport, updates, players, nsn, index + 1, skip, bits)
        }
        val other = players[playerIndex]
        val pendingUpdates = other?.let { updates[it.index] }
        val adding = viewport.shouldAdd(other)
        if (other == null || !adding) {
            viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
            return syncLowDefinition(viewport, updates, players, nsn, index + 1, skip + 1, bits)
        }
        val offset = bits.writeSkipCount(skip)
        bits.writeBit(true)
        bits.processLowDefinitionPlayer(viewport, other, playerIndex, pendingUpdates)
        return syncLowDefinition(viewport, updates, players, nsn, index + 1, offset, bits)
    }

    private fun BitAccess.processHighDefinitionPlayer(
        viewport: Viewport,
        other: Player?,
        index: Int,
        removing: Boolean,
        updates: ByteArray?
    ) {
        writeBits(1, if (removing) 0 else 1)

        if (!removing && updates != null) {
            viewport.updates[index] = updates
        }

        val moveDirection = other?.moveDirection

        when {
            removing -> { // remove the player
                // send a position update
                writeBits(2, 0)
                viewport.locations[index] = other?.lastLocation?.regionLocation ?: other?.location?.regionLocation ?: 0
                validateLocationChanges(viewport, other, index)
                viewport.players[index] = null
            }
            moveDirection != null -> {
                var dx = Direction.DIRECTION_DELTA_X[moveDirection.walkDirection!!.opcode]
                var dz = Direction.DIRECTION_DELTA_Z[moveDirection.walkDirection.opcode]
                var running = other.moveDirection!!.runDirection != null
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
            }
            updates != null -> {
                // send a block update
                writeBits(2, 0)
            }
        }
    }

    private fun BitAccess.processLowDefinitionPlayer(
        viewport: Viewport,
        other: Player,
        index: Int,
        updates: ByteArray?
    ) {
        // add an external player to start tracking
        writeBits(2, 0)
        validateLocationChanges(viewport, other, index)
        writeBits(13, other.location.x)
        writeBits(13, other.location.z)

        if (updates != null) {
            writeBits(1, 1)
            viewport.updates[index] = updates
        }

        viewport.players[other.index] = other
        viewport.nsnFlags[index] = viewport.nsnFlags[index] or 2
    }

    private fun BitAccess.validateLocationChanges(
        viewport: Viewport,
        other: Player?,
        index: Int
    ) {
        val currentPacked = viewport.locations[index]
        val packed = other?.location?.regionLocation ?: currentPacked
        val updating = other != null && packed != currentPacked
        writeBits(1, if (updating) 1 else 0)
        if (updating) {
            updateCoordinates(currentPacked, packed)
            viewport.locations[index] = packed
        }
    }

    private fun BitAccess.updateCoordinates(
        lastCoordinates: Int,
        currentCoordinates: Int
    ) {
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

    private fun Viewport.shouldAdd(other: Player?): Boolean = (other != null && other != player && other.location.withinDistance(player.location))
    private fun Viewport.shouldRemove(other: Player?): Boolean = (other == null || !other.online || !other.location.withinDistance(player.location) || !player.world.players.contains(other))
}
