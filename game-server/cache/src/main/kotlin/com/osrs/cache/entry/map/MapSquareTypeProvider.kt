package com.osrs.cache.entry.map

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule.MAP_INDEX
import com.osrs.cache.entry.EntryTypeProvider
import com.osrs.common.buffer.discard
import com.osrs.common.buffer.readIncrSmallSmart
import com.osrs.common.buffer.readShort
import com.osrs.common.buffer.readUByte
import com.osrs.common.buffer.readUShort
import com.osrs.common.buffer.readUShortSmart
import com.osrs.common.map.MapSquare
import com.osrs.common.map.MapSquares
import com.runetopic.cache.extension.decompress
import java.nio.ByteBuffer
import java.util.zip.ZipException

@Singleton
class MapSquareTypeProvider @Inject constructor(
    private val cache: Cache,
    private val mapSquares: MapSquares
) : EntryTypeProvider<MapSquareEntry>() {
    private val logger = InlineLogger()

    override fun loadTypeMap(): Map<Int, MapSquareEntry> = mapSquares.values.map(::loadMapEntry).associateBy(MapSquareEntry::id)

    private fun loadMapEntry(square: MapSquare): MapSquareEntry {
        val entry = MapSquareEntry(square.id)

        if (square.key.isEmpty()) {
            logger.warn { "The keys for this map square with the id ${square.id} are empty. Returning an empty map entry for this area." }
            return entry
        }

        val mapIndex = cache.index(MAP_INDEX)
        val terrain = mapIndex.group("m${entry.regionX}_${entry.regionZ}")
        val terrainData = terrain?.data?.decompress()?.buffer

        for (level in 0 until 4) {
            for (x in 0 until 64) {
                for (z in 0 until 64) {
                    entry.terrain[entry.pack(level, x, z)] = terrainData?.loadTerrain()
                }
            }
        }

        val locations = mapIndex.group("l${entry.regionX}_${entry.regionZ}")

        if (locations?.data?.isNotEmpty() == true) {
            try {
                locations.data.decompress(square.key).buffer.loadLocs(entry)
            } catch (exception: ZipException) {
                logger.warn { "Could not decompress and load locations from the cache. Perhaps the keys are incorrect. GroupId=${locations.id}, MapSquare=${square.id}." }
            }
        }
        return entry
    }

    private tailrec fun ByteBuffer.loadTerrain(
        height: Int = 0,
        overlayId: Int = 0,
        overlayPath: Int = 0,
        overlayRotation: Int = 0,
        collision: Int = 0,
        underlayId: Int = 0
    ): MapSquareTerrain = when (val opcode = readUShort()) {
        0 -> MapSquareTerrain(collision)
        1 -> {
            discard(1) // Height
            MapSquareTerrain(collision)
        }
        else -> loadTerrain(
            height = height,
            overlayId = if (opcode in 2..49) readShort() else overlayId,
            overlayPath = if (opcode in 2..49) (opcode - 2) / 4 else overlayPath,
            overlayRotation = if (opcode in 2..49) opcode - 2 and 3 else overlayRotation,
            collision = if (opcode in 50..81) opcode - 49 else collision,
            underlayId = if (opcode > 81) opcode - 81 else underlayId
        )
    }

    private tailrec fun ByteBuffer.loadLocs(entry: MapSquareEntry, locId: Int = -1) {
        val offset = readIncrSmallSmart()
        if (offset == 0) return
        loadLocationCollision(entry, locId + offset, 0)
        return loadLocs(entry, locId + offset)
    }

    private tailrec fun ByteBuffer.loadLocationCollision(entry: MapSquareEntry, locId: Int, packedLocation: Int) {
        val offset = readUShortSmart()
        if (offset == 0) return
        val attributes = readUByte()
        val shape = attributes shr 2
        val rotation = attributes and 0x3

        val packed = packedLocation + offset - 1
        val x = packed shr 6 and 0x3F
        val z = packed and 0x3F
        val level = (packed shr 12).let {
            if (entry.terrain[entry.packLevel1(x, z)]!!.collision and 0x2 == 2) it - 1 else it
        }
        val adjusted = (x and 0x3F shl 6) or (z and 0x3F) or (level shl 12)

        if (level >= 0) {
            entry.locations[adjusted] = when (val size = entry.locations[adjusted]?.size ?: 0) {
                0 -> arrayOfNulls(1)
                in 1 until 5 -> entry.locations[adjusted]!!.copyOf(size + 1)
                else -> entry.locations[adjusted]
            }
            entry.locations[adjusted]!![entry.locations[adjusted]!!.indexOf(null)] = MapSquareLocation(
                id = locId,
                x = x,
                z = z,
                level = level,
                shape = shape,
                rotation = rotation
            )
        }
        return loadLocationCollision(entry, locId, packed)
    }
}
