package com.osrs.common.skill

import kotlin.math.floor
import kotlin.math.pow

enum class Skill(val id: Int) {
    ATTACK(0),
    DEFENCE(1),
    STRENGTH(2),
    HITPOINTS(3),
    RANGED(4),
    PRAYER(5),
    MAGIC(6),
    COOKING(7),
    WOODCUTTING(8),
    FLETCHING(9),
    FISHING(10),
    FIREMAKING(11),
    CRAFTING(12),
    SMITHING(13),
    MINING(14),
    HERBLORE(15),
    AGILITY(16),
    THIEVING(17),
    SLAYER(18),
    FARMING(19),
    RUNECRAFTING(20),
    HUNTER(21),
    CONSTRUCTION(22)
    ;

    companion object {
        const val MAX_SKILLS = 25
        const val DEFAULT_HITPOINTS_LEVEL = 10
        const val DEFAULT_HERBLORE_LEVEL = 3
        const val DEFAULT_LEVEL = 1

        fun getXpForLevel(level: Int): Double {
            val summation = (1 until level.coerceIn(0, 127)).sumOf { floor(2.0.pow(it / 7.0) * 300 + it) }
            return floor(summation / 4.0).coerceIn(0.0, 200000000.0)
        }

        fun getLevelForXp(experience: Double, includeVirtualLevels: Boolean = false): Int {
            (0 until 126).reduce { totalXp, level ->
                val xpForLevel = floor(2.0.pow(level / 7.0) * 300 + level).toInt()
                if (!includeVirtualLevels && level >= 99) return level
                if (floor((totalXp + xpForLevel) / 4.0) >= experience) return level
                totalXp + xpForLevel
            }
            return if (includeVirtualLevels) 126 else 99
        }

        fun interpolate(level: Int, lowChance: Int, highChance: Int): Int {
            return lowChance + (highChance - lowChance) * (level - 1) / 98
        }

        fun valueOf(int: Int): Skill = values().first { it.id == int }

        fun valueOf(value: String): Skill {
            return when (value) {
                "Attack" -> ATTACK
                "Defence" -> DEFENCE
                "Strength" -> STRENGTH
                "Hitpoints" -> HITPOINTS
                "Ranged" -> RANGED
                "Prayer" -> PRAYER
                "Magic" -> MAGIC
                "Cooking" -> COOKING
                "Woodcutting" -> WOODCUTTING
                "Fletching" -> FLETCHING
                "Fishing" -> FISHING
                "Firemaking" -> FIREMAKING
                "Crafting" -> CRAFTING
                "Smithing" -> SMITHING
                "Mining" -> MINING
                "Herblore" -> HERBLORE
                "Agility" -> AGILITY
                "Thieving" -> THIEVING
                "Slayer" -> SLAYER
                "Farming" -> FARMING
                "Runecrafting" -> RUNECRAFTING
                "Hunter" -> HUNTER
                "Construction" -> CONSTRUCTION
                else -> throw IllegalArgumentException("Unhandled skill xlitekt.game.actor.skill.Skill.$value")
            }
        }
    }
}
