package com.osrs.game.actor.render.type

import com.osrs.game.actor.render.RenderType
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPart
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartColor

data class Appearance(
    val gender: Gender = Gender.MALE,
    val headIcon: Int,
    val skullIcon: Int,
    val transform: Int,
    val hidden: Boolean,
    val displayName: String,
    val bodyParts: IntArray = IntArray(7),
    val bodyPartColors: IntArray = IntArray(5),
    val renderSequences: IntArray = intArrayOf(808, 823, 819, 820, 821, 822, 824)
) : RenderType {
    init {
        bodyParts[BodyPart.HEAD] = 0
        bodyParts[BodyPart.JAW] = 10
        bodyParts[BodyPart.TORSO] = 18
        bodyParts[BodyPart.ARMS] = 26
        bodyParts[BodyPart.HANDS] = 33
        bodyParts[BodyPart.LEGS] = 36
        bodyParts[BodyPart.FEET] = 42
        bodyPartColors[BodyPartColor.HAIR] = 0
        bodyPartColors[BodyPartColor.TORSO] = 0
        bodyPartColors[BodyPartColor.LEGS] = 0
        bodyPartColors[BodyPartColor.FEET] = 0
        bodyPartColors[BodyPartColor.SKIN] = 0
    }

    fun isMale(): Boolean = gender == Gender.MALE
    fun isFemale(): Boolean = gender == Gender.FEMALE
}

enum class Gender(val id: Int) {
    MALE(0),
    FEMALE(1)
}
