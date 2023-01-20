package com.osrs.game.network.packet.server.builder.impl.sync

import com.osrs.common.buffer.BitAccessor
import com.osrs.common.buffer.buildPacket
import com.osrs.common.buffer.withBitAccess
import com.osrs.common.map.location.withinDistance
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.server.PlayerInfoPacket
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.readBytes
import kotlin.math.abs

fun Player.syncAndWritePlayers() {
    viewport.resize()

    val buffer = buildPacket {
        val blocks = BytePacketBuilder()
        repeat(2) { syncHighDefinition(this, blocks, it == 0) }
        repeat(2) { syncLowDefinition(this, blocks, it == 0) }
        writePacket(blocks.build())
    }.build()

    viewport.update()
    session.write(PlayerInfoPacket(buffer.readBytes()))
}

private fun Player.syncHighDefinition(buffer: BytePacketBuilder, updates: BytePacketBuilder, nsn: Boolean) {
    var skip = 0
    buffer.withBitAccess {
        for (i in 0 until viewport.highDefinitionsCount) {
            val playerIndex = viewport.highDefinitions[i]
            if (nsn == (0x1 and viewport.nsnFlags[playerIndex] != 0)) continue
            if (skip > 0) {
                viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
                skip--
                continue
            }
            val other = viewport.players[playerIndex]
            val removing = shouldRemove(other)
            val moving = other?.walkDirection != null
            val updating = shouldUpdate(other) || moving
            val active = removing || updating
            writeBit(active)
            if (active) {
                processHighDefinitionPlayer(this, updates, other, playerIndex, removing, updating, moving)
            } else {
                for (index in i + 1 until viewport.highDefinitionsCount) {
                    val localIndex = viewport.highDefinitions[index]
                    if (nsn == (0x1 and viewport.nsnFlags[localIndex] != 0)) continue
                    val localPlayer = viewport.players[localIndex]
                    if (shouldRemove(localPlayer) || (localPlayer != null && localPlayer.renderer.hasPendingUpdate())) break
                    skip++
                }

                writeSkipCount(skip)
                viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
            }
        }
    }

    if (skip > 0) throw RuntimeException("Skip count was greater than zero for high definition.")
}

private fun Player.syncLowDefinition(builder: BytePacketBuilder, blocks: BytePacketBuilder, nsn: Boolean) {
    var skip = 0
    builder.withBitAccess {
        viewport.let { viewport ->
            for (i in 0 until viewport.lowDefinitionsCount) {
                val playerIndex = viewport.lowDefinitions[i]
                if (nsn == (0x1 and viewport.nsnFlags[playerIndex] == 0)) continue
                if (skip > 0) {
                    viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
                    skip--
                    continue
                }
                val other = world.players[playerIndex]
                val adding = shouldAdd(other)
                writeBit(adding)
                if (adding) {
                    processLowDefinitionPlayer(adding, other!!, playerIndex, this, blocks)
                } else {
                    for (index in i + 1 until viewport.lowDefinitionsCount) {
                        val externalIndex = viewport.lowDefinitions[index]
                        if (nsn == (0x1 and viewport.nsnFlags[externalIndex] == 0)) continue
                        val externalPlayer = world.players[externalIndex]
                        if (shouldAdd(externalPlayer)) break
                        skip++
                    }
                    writeSkipCount(skip)
                    viewport.nsnFlags[playerIndex] = viewport.nsnFlags[playerIndex] or 2
                }
            }
        }
    }

    if (skip > 0) throw RuntimeException("Skip count was greater than zero for low definition.")
}

private fun Player.processHighDefinitionPlayer(
    builder: BitAccessor,
    blocks: BytePacketBuilder,
    other: Player?,
    index: Int,
    removing: Boolean,
    updating: Boolean,
    moving: Boolean
) {
    builder.writeBits(1, if (removing) 0 else 1)

    if (updating) {
        encodePendingBlocks(false, other!!, blocks)
    }

    when {
        removing -> { // remove the player
            // send a position update
            builder.writeBits(2, 0)
            viewport.locations[index] = other?.lastLocation?.regionLocation ?: other?.location?.regionLocation ?: 0
            validateLocationChanges(builder, other, index)
            viewport.players[index] = null
        }
        moving -> {
            val dx = DIRECTION_DELTA_X[other!!.walkDirection!!.playerOpcode]
            val dz = DIRECTION_DELTA_Z[other!!.walkDirection!!.playerOpcode]
            val direction = getPlayerWalkingDirection(dx, dz)
            builder.writeBits(2, 1) // 2 for running
            builder.writeBits(3, direction) // Opcode for direction bit 3 for walking bit 4 for running.
        }
        updating -> {
            // send a block update
            builder.writeBits(2, 0)
        }
    }
}

private fun Player.processLowDefinitionPlayer(
    adding: Boolean,
    other: Player,
    index: Int,
    builder: BitAccessor,
    blocks: BytePacketBuilder
) {
    if (adding) {
        // add an external player to start tracking
        builder.writeBits(2, 0)
        validateLocationChanges(builder, other, index)
        builder.writeBits(13, other.location.x)
        builder.writeBits(13, other.location.z)
        // send a force block update
        builder.writeBits(1, 1)
        encodePendingBlocks(true, other, blocks)
        viewport.players[other.index] = other
        viewport.nsnFlags[index] = viewport.nsnFlags[index] or 2
    }
}

private fun Player.validateLocationChanges(
    builder: BitAccessor,
    other: Player?,
    index: Int
) {
    val currentPacked = viewport.locations[index]
    val packed = other?.location?.regionLocation ?: currentPacked
    val updating = other != null && packed != currentPacked
    builder.writeBits(1, if (updating) 1 else 0)
    if (updating) {
        updateCoordinates(builder, currentPacked, packed)
        viewport.locations[index] = packed
    }
}

private fun updateCoordinates(
    builder: BitAccessor,
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
        builder.writeBits(2, 1)
        builder.writeBits(2, deltaPlane)
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
        builder.writeBits(2, 2)
        builder.writeBits(5, (deltaPlane shl 3) + (opcode and 0x7))
    } else {
        builder.writeBits(2, 3)
        builder.writeBits(18, (deltaZ and 0xff) + (deltaX and 0xff shl 8) + (deltaPlane shl 16))
    }
}

private fun BitAccessor.writeSkipCount(
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

private fun encodePendingBlocks(forceOtherUpdate: Boolean, other: Player, blocks: BytePacketBuilder) {
    if (forceOtherUpdate) other.refreshAppearance(other.appearance)
    var mask = 0x0
    other.renderer.updates.forEach { mask = mask or it.mask }
    if (mask >= 0xff) mask = mask or 0x40
    blocks.writeByte(mask.toByte())
    if (mask >= 0xff) blocks.writeByte((mask shr 8).toByte())
    other.renderer.updates.forEach { blocks.writePacket(it.build(other)) }
}

private fun shouldUpdate(other: Player?): Boolean = other?.renderer?.hasPendingUpdate() ?: false
private fun Player.shouldAdd(other: Player?): Boolean = (other != null && other != this && other.location.withinDistance(location))
private fun Player.shouldRemove(other: Player?): Boolean = (other == null || !other.location.withinDistance(location) || !world.players.contains(this))

val DIRECTION_DELTA_X = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
val DIRECTION_DELTA_Z = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)

fun getPlayerWalkingDirection(dx: Int, dy: Int): Int {
    if (dx == -1 && dy == -1) {
        return 0
    }
    if (dx == 0 && dy == -1) {
        return 1
    }
    if (dx == 1 && dy == -1) {
        return 2
    }
    if (dx == -1 && dy == 0) {
        return 3
    }
    if (dx == 1 && dy == 0) {
        return 4
    }
    if (dx == -1 && dy == 1) {
        return 5
    }
    if (dx == 0 && dy == 1) {
        return 6
    }
    return if (dx == 1 && dy == 1) {
        7
    } else -1
}
