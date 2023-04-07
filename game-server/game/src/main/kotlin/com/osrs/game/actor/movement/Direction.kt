package com.osrs.game.actor.movement

import com.osrs.common.map.location.Location

enum class Direction(val opcode: Int, val npcOpcode: Int) {
    NONE(opcode = -1, npcOpcode = -1),

    NORTH_WEST(opcode = 5, npcOpcode = 0),

    NORTH(opcode = 6, npcOpcode = 1),

    NORTH_EAST(opcode = 7, npcOpcode = 2),

    WEST(opcode = 3, npcOpcode = 3),

    EAST(opcode = 4, npcOpcode = 4),

    SOUTH_WEST(opcode = 0, npcOpcode = 5),

    SOUTH(opcode = 1, npcOpcode = 6),

    SOUTH_EAST(opcode = 2, npcOpcode = 7);

    fun getDeltaX(): Int = when (this) {
        SOUTH_EAST, NORTH_EAST, EAST -> 1
        SOUTH_WEST, NORTH_WEST, WEST -> -1
        else -> 0
    }

    fun getDeltaZ(): Int = when (this) {
        NORTH_WEST, NORTH_EAST, NORTH -> 1
        SOUTH_WEST, SOUTH_EAST, SOUTH -> -1
        else -> 0
    }

    companion object {
        fun to(from: Location, to: Location): Direction {
            val deltaX = to.x - from.x
            val deltaZ = to.z - from.z
            return getDirection(deltaX, deltaZ)
        }

        fun getDirection(dx: Int, dz: Int): Direction {
            when (dz) {
                1 -> when (dx) {
                    1 -> return NORTH_EAST
                    0 -> return NORTH
                    -1 -> return NORTH_WEST
                }
                -1 -> when (dx) {
                    1 -> return SOUTH_EAST
                    0 -> return SOUTH
                    -1 -> return SOUTH_WEST
                }
                0 -> when (dx) {
                    1 -> return EAST
                    0 -> return NONE
                    -1 -> return WEST
                }
            }
            return NONE
        }

        val DIRECTION_DELTA_X = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
        val DIRECTION_DELTA_Z = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)

        fun getPlayerWalkingDirection(dx: Int, dz: Int): Int {
            if (dx == -1 && dz == -1) {
                return 0
            }
            if (dx == 0 && dz == -1) {
                return 1
            }
            if (dx == 1 && dz == -1) {
                return 2
            }
            if (dx == -1 && dz == 0) {
                return 3
            }
            if (dx == 1 && dz == 0) {
                return 4
            }
            if (dx == -1 && dz == 1) {
                return 5
            }
            if (dx == 0 && dz == 1) {
                return 6
            }
            return if (dx == 1 && dz == 1) {
                7
            } else {
                -1
            }
        }

        fun getPlayerRunningDirection(dx: Int, dz: Int): Int {
            if (dx == -2 && dz == -2) {
                return 0
            }
            if (dx == -1 && dz == -2) {
                return 1
            }
            if (dx == 0 && dz == -2) {
                return 2
            }
            if (dx == 1 && dz == -2) {
                return 3
            }
            if (dx == 2 && dz == -2) {
                return 4
            }
            if (dx == -2 && dz == -1) {
                return 5
            }
            if (dx == 2 && dz == -1) {
                return 6
            }
            if (dx == -2 && dz == 0) {
                return 7
            }
            if (dx == 2 && dz == 0) {
                return 8
            }
            if (dx == -2 && dz == 1) {
                return 9
            }
            if (dx == 2 && dz == 1) {
                return 10
            }
            if (dx == -2 && dz == 2) {
                return 11
            }
            if (dx == -1 && dz == 2) {
                return 12
            }
            if (dx == 0 && dz == 2) {
                return 13
            }
            if (dx == 1 && dz == 2) {
                return 14
            }
            return if (dx == 2 && dz == 2) 15 else -1
        }
    }
}
