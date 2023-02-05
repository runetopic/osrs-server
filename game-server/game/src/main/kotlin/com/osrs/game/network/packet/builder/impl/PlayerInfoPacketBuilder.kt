package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.BitAccess
import com.osrs.common.buffer.buildPacket
import com.osrs.common.buffer.withBitAccess
import com.osrs.common.buffer.writeBytes
import com.osrs.common.map.location.withinDistance
import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.movement.Direction
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.PlayerInfoPacket
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.writeFully
import java.nio.ByteBuffer
import kotlin.math.abs

@Singleton
class PlayerInfoPacketBuilder : PacketBuilder<PlayerInfoPacket>(
    opcode = 77,
    size = -2
) {
    override fun build(packet: PlayerInfoPacket, buffer: ByteBuffer) {
        packet.viewport.resize()

        buffer.writeBytes(
            buildPacket {
                repeat(2) {
                    buffer.syncHighDefinition(
                        packet.viewport,
                        this,
                        packet.highDefinitionUpdates,
                        it == 0
                    )
                }
                repeat(2) {
                    buffer.syncLowDefinition(
                        packet.viewport,
                        this,
                        packet.lowDefinitionUpdates,
                        packet.players,
                        it == 0
                    )
                }
            }.build().readBytes()
        )

        packet.viewport.update()
    }

    private fun ByteBuffer.syncLowDefinition(
        viewport: Viewport,
        blocks: BytePacketBuilder,
        updates: Array<ByteArray?>,
        players: PlayerList,
        nsn: Boolean
    ) = withBitAccess {
        var skip = 0

        for (i in 0 until viewport.lowDefinitionsCount) {
            val playerIndex = viewport.lowDefinitions[i]
            if (nsn == (0x1 and viewport.nsnFlags[playerIndex] == 0)) continue
            if (skip > 0) {
                viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
                skip--
                continue
            }
            val other = players[playerIndex]
            val pendingUpdates = other?.let { updates[it.index] }
            val adding = viewport.shouldAdd(other)
            writeBit(adding)
            if (other != null && adding) {
                processLowDefinitionPlayer(viewport, other, playerIndex, blocks, pendingUpdates)
            } else {
                for (index in i + 1 until viewport.lowDefinitionsCount) {
                    val externalIndex = viewport.lowDefinitions[index]
                    if (nsn == (0x1 and viewport.nsnFlags[externalIndex] == 0)) continue
                    val externalPlayer = players[externalIndex]
                    if (viewport.shouldAdd(externalPlayer)) break
                    skip++
                }
                writeSkipCount(skip)
                viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
            }
        }

        if (skip > 0) throw RuntimeException("Skip count was greater than zero for low definition.")
    }

    private fun BitAccess.processLowDefinitionPlayer(
        viewport: Viewport,
        other: Player,
        index: Int,
        blocks: BytePacketBuilder,
        updates: ByteArray?
    ) {
        // add an external player to start tracking
        writeBits(2, 0)
        validateLocationChanges(viewport, other, index)
        writeBits(13, other.location.x)
        writeBits(13, other.location.z)

        if (updates != null) {
            writeBits(1, 1)
            blocks.writeFully(updates)
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

    private fun BitAccess.writeSkipCount(
        count: Int
    ) {
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
    }

    private fun ByteBuffer.syncHighDefinition(viewport: Viewport, blocks: BytePacketBuilder, updates: Array<ByteArray?>, nsn: Boolean) = withBitAccess {
        var skip = 0
        for (i in 0 until viewport.highDefinitionsCount) {
            val playerIndex = viewport.highDefinitions[i]
            if (nsn == (0x1 and viewport.nsnFlags[playerIndex] != 0)) continue
            if (skip > 0) {
                viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
                skip--
                continue
            }
            val other = viewport.players[playerIndex]
            val removing = viewport.shouldRemove(other)
            val pendingUpdates = other?.let { updates[it.index] }
            val updating = pendingUpdates != null || other?.moveDirection != null
            val active = removing || updating
            writeBit(active)
            if (active) {
                processHighDefinitionPlayer(viewport, blocks, other, playerIndex, removing, pendingUpdates)
            } else {
                for (index in i + 1 until viewport.highDefinitionsCount) {
                    val localIndex = viewport.highDefinitions[index]
                    if (nsn == (0x1 and viewport.nsnFlags[localIndex] != 0)) continue
                    val localPlayer = viewport.players[localIndex]
                    val localPlayerUpdates = localPlayer?.let { updates[it.index] }
                    if (viewport.shouldRemove(localPlayer) || localPlayer?.moveDirection != null || (localPlayer != null && localPlayerUpdates != null)) break
                    skip++
                }

                writeSkipCount(skip)
                viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
            }
        }

        if (skip > 0) throw RuntimeException("Skip count was greater than zero for high definition.")
    }


    private fun BitAccess.processHighDefinitionPlayer(
        viewport: Viewport,
        blocks: BytePacketBuilder,
        other: Player?,
        index: Int,
        removing: Boolean,
        updates: ByteArray?
    ) {
        writeBits(1, if (removing) 0 else 1)

        if (!removing && updates != null) blocks.writeFully(updates)

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

    private fun Viewport.shouldAdd(other: Player?): Boolean = (other != null && other != player && other.location.withinDistance(player.location))
    private fun Viewport.shouldRemove(other: Player?): Boolean = (other == null || !other.online || !other.location.withinDistance(player.location) || !player.world.players.contains(other))
}
