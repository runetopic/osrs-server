package com.osrs.game.world.map

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.api.map.location.Location
import com.osrs.api.map.location.transform
import com.osrs.cache.entry.config.location.LocationEntryProvider
import com.osrs.cache.entry.map.MapSquareEntry
import com.osrs.cache.entry.map.MapSquareLocation
import com.osrs.cache.entry.map.MapSquareTerrain
import com.osrs.game.actor.movement.Direction
import com.osrs.game.world.map.zone.ZoneManager
import org.rsmod.pathfinder.StepValidator
import org.rsmod.pathfinder.ZoneFlags
import org.rsmod.pathfinder.flag.CollisionFlag.BLOCK_NPCS
import org.rsmod.pathfinder.flag.CollisionFlag.FLOOR
import org.rsmod.pathfinder.flag.CollisionFlag.FLOOR_DECORATION
import org.rsmod.pathfinder.flag.CollisionFlag.OBJECT
import org.rsmod.pathfinder.flag.CollisionFlag.OBJECT_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.OBJECT_ROUTE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_EAST
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_EAST_ROUTE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH_EAST
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH_EAST_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH_EAST_ROUTE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH_ROUTE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH_WEST
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH_WEST_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_NORTH_WEST_ROUTE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH_EAST
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH_EAST_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH_EAST_ROUTE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH_ROUTE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH_WEST
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH_WEST_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_SOUTH_WEST_ROUTE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_WEST
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER
import org.rsmod.pathfinder.flag.CollisionFlag.WALL_WEST_ROUTE_BLOCKER

@Singleton
class CollisionMap @Inject constructor(
    private val locations: LocationEntryProvider,
    private val zoneFlags: ZoneFlags,
    private val stepValidator: StepValidator
) {
    fun canTravel(location: Location, direction: Direction, isNPC: Boolean): Boolean =
        stepValidator.canTravel(location.level, location.x, location.z, direction.getDeltaX(), direction.getDeltaZ(), 1, if (isNPC) BLOCK_NPCS else 0)

    fun applyCollision(entry: MapSquareEntry) {
        val area = MapSquareEntry.AREA
        repeat(4 * area) { index ->
            val remaining = index % area
            val level = index / area
            val x = remaining / 64
            val z = remaining % 64
            val terrain = MapSquareTerrain(entry.terrain[entry.pack(level, x, z)])
            if ((terrain.collision and 0x1) != 0x1) {
                return@repeat
            }
            val actualLevel = if ((MapSquareTerrain(entry.terrain[entry.pack(1, x, z)]).collision and 0x2) == 0x2) level - 1 else level
            if (actualLevel < 0) {
                return@repeat
            }
            val baseX = entry.regionX shl 6
            val baseZ = entry.regionZ shl 6
            val location = Location(x + baseX, z + baseZ, actualLevel)
            addFloorCollision(location)
            ZoneManager[location.zoneLocation]
        }

        for (packed in entry.locations) {
            val loc = MapSquareLocation(packed)
            // val terrain = MapSquareTerrain(entry.terrain[entry.pack(loc.level, loc.x, loc.z)])
            // if ((terrain.collision and 0x1) != 0x1) continue
            val actualLevel = if ((MapSquareTerrain(entry.terrain[entry.pack(1, loc.x, loc.z)]).collision and 0x2) == 0x2) loc.level - 1 else loc.level
            if (actualLevel < 0) continue
            val baseX = entry.regionX shl 6
            val baseZ = entry.regionZ shl 6
            val location = Location(loc.x + baseX, loc.z + baseZ, actualLevel)
            val gameObject = GameObject(loc.id, location, loc.shape, loc.rotation)
            addObjectCollision(gameObject)
            ZoneManager[location.zoneLocation].addStaticLoc(gameObject)
        }
    }

    fun addActorCollision(location: Location) {
        addCollisionFlag(location, BLOCK_NPCS, true)
    }

    fun removeActorCollision(location: Location) {
        addCollisionFlag(location, BLOCK_NPCS, false)
    }

    fun collisionFlag(location: Location) = zoneFlags[location.x, location.z, location.level]
    fun addObjectCollision(obj: GameObject) = changeNormalCollision(obj, true)
    fun removeObjectCollision(obj: GameObject) = changeNormalCollision(obj, false)

    private fun changeNormalCollision(obj: GameObject, add: Boolean) {
        val entry = locations[obj.id] ?: return
        val shape = obj.shape
        val location = obj.location
        val rotation = obj.rotation
        val interactType = entry.interactType
        val blockProjectile = entry.blockProjectile
        val breakRouteFinding = entry.breakRouteFinding

        when {
            shape in GameObjectShape.WALL_SHAPES && interactType != 0 -> {
                changeWallCollision(
                    location,
                    rotation,
                    shape,
                    blockProjectile,
                    !breakRouteFinding,
                    add
                )
            }
            shape in GameObjectShape.NORMAL_SHAPES && interactType != 0 -> {
                var width = entry.width
                var length = entry.height
                if (rotation == 1 || rotation == 3) {
                    width = entry.height
                    length = entry.width
                }
                changeNormalCollision(
                    location,
                    width,
                    length,
                    blockProjectile,
                    !breakRouteFinding,
                    add
                )
            }
            shape in GameObjectShape.GROUND_DECOR_SHAPES && interactType == 1 -> changeFloorDecor(location, add)
        }
    }

    private fun changeNormalCollision(
        location: Location,
        width: Int,
        length: Int,
        blocksProjectile: Boolean,
        breakRouteFinding: Boolean,
        add: Boolean
    ) {
        var flag = OBJECT

        if (blocksProjectile) {
            flag = flag or OBJECT_PROJECTILE_BLOCKER
        }

        if (breakRouteFinding) {
            flag = flag or OBJECT_ROUTE_BLOCKER
        }

        for (x in 0 until width) {
            for (y in 0 until length) {
                val translate = location.transform(x, y)
                addCollisionFlag(translate, flag, add)
            }
        }
    }

    private fun changeWallRouteFinding(
        location: Location,
        rotation: Int,
        shape: Int,
        add: Boolean
    ) {
        when (shape) {
            GameObjectShape.WALL_STRAIGHT -> when (rotation) {
                0 -> {
                    addCollisionFlag(location, WALL_WEST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST_ROUTE_BLOCKER, add)
                }
                1 -> {
                    addCollisionFlag(location, WALL_NORTH_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH_ROUTE_BLOCKER, add)
                }
                2 -> {
                    addCollisionFlag(location, WALL_EAST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST_ROUTE_BLOCKER, add)
                }
                3 -> {
                    addCollisionFlag(location, WALL_SOUTH_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH_ROUTE_BLOCKER, add)
                }
            }
            GameObjectShape.WALL_DIAGONALCORNER, GameObjectShape.WALL_SQUARECORNER -> when (rotation) {
                0 -> {
                    addCollisionFlag(location, WALL_NORTH_WEST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, 1), WALL_SOUTH_EAST_ROUTE_BLOCKER, add)
                }
                1 -> {
                    addCollisionFlag(location, WALL_NORTH_EAST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, 1), WALL_SOUTH_WEST_ROUTE_BLOCKER, add)
                }
                2 -> {
                    addCollisionFlag(location, WALL_SOUTH_EAST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, -1), WALL_NORTH_WEST_ROUTE_BLOCKER, add)
                }
                3 -> {
                    addCollisionFlag(location, WALL_SOUTH_WEST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, -1), WALL_NORTH_EAST_ROUTE_BLOCKER, add)
                }
            }
            GameObjectShape.WALL_L -> when (rotation) {
                0 -> {
                    addCollisionFlag(location, WALL_NORTH_ROUTE_BLOCKER or WALL_WEST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH_ROUTE_BLOCKER, add)
                }
                1 -> {
                    addCollisionFlag(location, WALL_NORTH_ROUTE_BLOCKER or WALL_EAST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST_ROUTE_BLOCKER, add)
                }
                2 -> {
                    addCollisionFlag(location, WALL_SOUTH_ROUTE_BLOCKER or WALL_EAST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH_ROUTE_BLOCKER, add)
                }
                3 -> {
                    addCollisionFlag(location, WALL_SOUTH_ROUTE_BLOCKER or WALL_WEST_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH_ROUTE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST_ROUTE_BLOCKER, add)
                }
            }
        }
    }

    private fun changeWallCollision(
        location: Location,
        rotation: Int,
        shape: Int,
        blockProjectile: Boolean,
        breakRouteFinding: Boolean,
        add: Boolean
    ) {
        changeWallCollision(location, rotation, shape, add)
        if (blockProjectile) changeWallProjectileCollision(location, rotation, shape, add)
        if (breakRouteFinding) changeWallRouteFinding(location, rotation, shape, add)
    }

    private fun changeWallProjectileCollision(location: Location, rotation: Int, shape: Int, add: Boolean) {
        when (shape) {
            GameObjectShape.WALL_STRAIGHT -> when (rotation) {
                0 -> {
                    addCollisionFlag(location, WALL_WEST_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST_PROJECTILE_BLOCKER, add)
                }
                1 -> {
                    addCollisionFlag(location, WALL_NORTH_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH_PROJECTILE_BLOCKER, add)
                }
                2 -> {
                    addCollisionFlag(location, WALL_EAST_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST_PROJECTILE_BLOCKER, add)
                }
                3 -> {
                    addCollisionFlag(location, WALL_SOUTH_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH_PROJECTILE_BLOCKER, add)
                }
            }
            GameObjectShape.WALL_DIAGONALCORNER, GameObjectShape.WALL_SQUARECORNER -> when (rotation) {
                0 -> {
                    addCollisionFlag(location, WALL_NORTH_WEST_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, 1), WALL_SOUTH_EAST_PROJECTILE_BLOCKER, add)
                }
                1 -> {
                    addCollisionFlag(location, WALL_NORTH_EAST_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, 1), WALL_SOUTH_WEST_PROJECTILE_BLOCKER, add)
                }
                2 -> {
                    addCollisionFlag(location, WALL_SOUTH_EAST_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, -1), WALL_NORTH_WEST_PROJECTILE_BLOCKER, add)
                }
                3 -> {
                    addCollisionFlag(location, WALL_SOUTH_WEST_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, -1), WALL_NORTH_EAST_PROJECTILE_BLOCKER, add)
                }
            }
            GameObjectShape.WALL_L -> when (rotation) {
                0 -> {
                    val flag = WALL_WEST_PROJECTILE_BLOCKER or WALL_NORTH_PROJECTILE_BLOCKER
                    addCollisionFlag(location, flag, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH_PROJECTILE_BLOCKER, add)
                }
                1 -> {
                    val flag = WALL_NORTH_PROJECTILE_BLOCKER or WALL_EAST_PROJECTILE_BLOCKER
                    addCollisionFlag(location, flag, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST_PROJECTILE_BLOCKER, add)
                }
                2 -> {
                    val flag = WALL_EAST_PROJECTILE_BLOCKER or WALL_SOUTH_PROJECTILE_BLOCKER
                    addCollisionFlag(location, flag, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH_PROJECTILE_BLOCKER, add)
                }
                3 -> {
                    val flag = WALL_SOUTH_PROJECTILE_BLOCKER or WALL_WEST_PROJECTILE_BLOCKER
                    addCollisionFlag(location, flag, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH_PROJECTILE_BLOCKER, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST_PROJECTILE_BLOCKER, add)
                }
            }
        }
    }

    private fun changeWallCollision(
        location: Location,
        rotation: Int,
        shape: Int,
        add: Boolean
    ) {
        when (shape) {
            GameObjectShape.WALL_STRAIGHT -> when (rotation) {
                0 -> {
                    addCollisionFlag(location, WALL_WEST, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST, add)
                }
                1 -> {
                    addCollisionFlag(location, WALL_NORTH, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH, add)
                }
                2 -> {
                    addCollisionFlag(location, WALL_EAST, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST, add)
                }
                3 -> {
                    addCollisionFlag(location, WALL_SOUTH, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH, add)
                }
            }
            GameObjectShape.WALL_DIAGONALCORNER, GameObjectShape.WALL_SQUARECORNER -> when (rotation) {
                0 -> {
                    addCollisionFlag(location, WALL_NORTH_WEST, add)
                    addCollisionFlag(location.transform(-1, 1), WALL_SOUTH_EAST, add)
                }
                1 -> {
                    addCollisionFlag(location, WALL_NORTH_EAST, add)
                    addCollisionFlag(location.transform(1, 1), WALL_SOUTH_WEST, add)
                }
                2 -> {
                    addCollisionFlag(location, WALL_SOUTH_EAST, add)
                    addCollisionFlag(location.transform(1, -1), WALL_NORTH_WEST, add)
                }
                3 -> {
                    addCollisionFlag(location, WALL_SOUTH_WEST, add)
                    addCollisionFlag(location.transform(-1, -1), WALL_NORTH_EAST, add)
                }
            }
            GameObjectShape.WALL_L -> when (rotation) {
                0 -> {
                    addCollisionFlag(location, WALL_NORTH or WALL_WEST, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH, add)
                }
                1 -> {
                    addCollisionFlag(location, WALL_NORTH or WALL_EAST, add)
                    addCollisionFlag(location.transform(0, 1), WALL_SOUTH, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST, add)
                }
                2 -> {
                    addCollisionFlag(location, WALL_SOUTH or WALL_EAST, add)
                    addCollisionFlag(location.transform(1, 0), WALL_WEST, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH, add)
                }
                3 -> {
                    addCollisionFlag(location, WALL_SOUTH or WALL_WEST, add)
                    addCollisionFlag(location.transform(0, -1), WALL_NORTH, add)
                    addCollisionFlag(location.transform(-1, 0), WALL_EAST, add)
                }
            }
        }
    }

    private fun changeFloorDecor(coords: Location, add: Boolean) = addCollisionFlag(coords, FLOOR_DECORATION, add)

    private fun addCollisionFlag(location: Location, mask: Int, add: Boolean) = when {
        add -> zoneFlags.add(location.x, location.z, location.level, mask)
        else -> zoneFlags.remove(location.x, location.z, location.level, mask)
    }

    private fun addFloorCollision(location: Location) = addCollisionFlag(location, FLOOR, true)
}
