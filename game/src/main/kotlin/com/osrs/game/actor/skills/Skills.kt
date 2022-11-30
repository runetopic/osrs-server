package com.osrs.game.actor.skills

import com.osrs.database.account.serializer.SkillsSerializer
import com.osrs.game.actor.skills.Skill.Companion.DEFAULT_HERBLORE_LEVEL
import com.osrs.game.actor.skills.Skill.Companion.DEFAULT_HITPOINTS_LEVEL
import com.osrs.game.actor.skills.Skill.Companion.DEFAULT_LEVEL
import com.osrs.game.actor.skills.Skill.Companion.MAX_SKILLS
import com.osrs.game.actor.skills.Skill.Companion.getLevelForXp
import com.osrs.game.actor.skills.Skill.Companion.getXpForLevel
import kotlinx.serialization.Serializable

@Serializable(with = SkillsSerializer::class)
class Skills(
    private val levels: IntArray = defaultLevelsAndExperience.first,
    private val experience: DoubleArray = defaultLevelsAndExperience.second
) {
    fun level(skill: Skill): Int = levels[skill.id]
    fun xp(skill: Skill): Double = experience[skill.id]

    internal fun addExperience(skill: Skill, experience: Double, function: (level: Int, experience: Double) -> Unit) {
        val newExperience = this.xp(skill) + experience
        val level = getLevelForXp(newExperience)

        this.experience[skill.id] = newExperience

        if (this.levels[skill.id] != level)
            this.levels[skill.id] = level

        function.invoke(level, newExperience)
    }

    companion object {
        fun defaultLevelsAndExperience(): Pair<IntArray, DoubleArray> {
            val defaultExperience = getXpForLevel(DEFAULT_LEVEL)

            val levels = IntArray(MAX_SKILLS)
            val experience = DoubleArray(MAX_SKILLS)

            Skill.values().forEach {
                val (level, xp) = when (it) {
                    Skill.HITPOINTS -> DEFAULT_HITPOINTS_LEVEL to getXpForLevel(DEFAULT_HITPOINTS_LEVEL)
                    Skill.HERBLORE -> DEFAULT_HERBLORE_LEVEL to getXpForLevel(DEFAULT_HERBLORE_LEVEL)
                    else -> DEFAULT_LEVEL to defaultExperience
                }
                levels[it.id] = level
                experience[it.id] = xp
            }
            return Pair(levels, experience)
        }

        val defaultLevelsAndExperience = defaultLevelsAndExperience()
    }
}
