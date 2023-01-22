package com.osrs.game.actor.movement

import com.osrs.common.map.location.Location

enum class Direction(val opcode: Int) {
    NONE(opcode = -1),

    NORTH_WEST(opcode = 5),

    NORTH(opcode = 6),

    NORTH_EAST(opcode = 7),

    WEST(opcode = 3),

    EAST(opcode = 4),

    SOUTH_WEST(opcode = 0),

    SOUTH(opcode = 1),

    SOUTH_EAST(opcode = 2);

    companion object {
        fun to(from: Location, to: Location): Direction {
            val deltaX = to.x - from.x
            val deltaZ = to.z - from.z
            return fromDeltas(deltaX, deltaZ)
        }

        private fun fromDeltas(deltaX: Int, deltaZ: Int): Direction {
            when (deltaZ) {
                1 -> when (deltaX) {
                    1 -> return NORTH_EAST
                    0 -> return NORTH
                    -1 -> return NORTH_WEST
                }
                -1 -> when (deltaX) {
                    1 -> return SOUTH_EAST
                    0 -> return SOUTH
                    -1 -> return SOUTH_WEST
                }
                0 -> when (deltaX) {
                    1 -> return EAST
                    0 -> return NONE
                    -1 -> return WEST
                }
            }
            throw IllegalArgumentException("Unhandled direction delta [$deltaX, $deltaZ]")
        }

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

        fun getPlayerRunningDirection(dx: Int, dy: Int): Int {
            if (dx == -2 && dy == -2) {
                return 0
            }
            if (dx == -1 && dy == -2) {
                return 1
            }
            if (dx == 0 && dy == -2) {
                return 2
            }
            if (dx == 1 && dy == -2) {
                return 3
            }
            if (dx == 2 && dy == -2) {
                return 4
            }
            if (dx == -2 && dy == -1) {
                return 5
            }
            if (dx == 2 && dy == -1) {
                return 6
            }
            if (dx == -2 && dy == 0) {
                return 7
            }
            if (dx == 2 && dy == 0) {
                return 8
            }
            if (dx == -2 && dy == 1) {
                return 9
            }
            if (dx == 2 && dy == 1) {
                return 10
            }
            if (dx == -2 && dy == 2) {
                return 11
            }
            if (dx == -1 && dy == 2) {
                return 12
            }
            if (dx == 0 && dy == 2) {
                return 13
            }
            if (dx == 1 && dy == 2) {
                return 14
            }
            return if (dx == 2 && dy == 2) 15 else -1
        }
    }
}