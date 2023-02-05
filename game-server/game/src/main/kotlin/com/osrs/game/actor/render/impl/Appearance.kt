package com.osrs.game.actor.render.impl

import com.osrs.game.actor.render.RenderType
import com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit.BodyPart
import com.osrs.game.network.packet.builder.impl.sync.player.appearance.kit.BodyPartColor
import java.util.*

data class Appearance(
    val gender: Gender = Gender.MALE,
    val headIcon: Int,
    val skullIcon: Int,
    val transform: Int,
    val hidden: Boolean
) : RenderType {
    enum class Gender(val mask: Int) { MALE(0x0), FEMALE(0x1); }

    val bodyParts = EnumMap<BodyPart, Int>(BodyPart::class.java)
    val bodyPartColors = EnumMap<BodyPartColor, Int>(BodyPartColor::class.java)

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


