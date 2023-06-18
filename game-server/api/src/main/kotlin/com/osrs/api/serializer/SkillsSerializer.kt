package com.osrs.api.serializer

import com.osrs.api.skill.Skill
import com.osrs.api.skill.Skills
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.util.Locale

class SkillsSerializer : KSerializer<Skills> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("skills") {
            element<Map<String, Int>>("levels")
            element<Map<String, Double>>("experience")
        }

    override fun deserialize(decoder: Decoder): Skills = decoder.decodeStructure(descriptor) {
        decodeElementIndex(descriptor)
        val levels = decodeSerializableElement(
            descriptor,
            0,
            MapSerializer(String.serializer(), Int.serializer())
        ).values.toIntArray()
        decodeElementIndex(descriptor)
        val experience = decodeSerializableElement(
            descriptor,
            1,
            MapSerializer(String.serializer(), Double.serializer())
        ).values.toDoubleArray()
        return@decodeStructure Skills(levels, experience)
    }

    override fun serialize(encoder: Encoder, value: Skills) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(
            descriptor,
            0,
            MapSerializer(String.serializer(), Int.serializer()),
            Skill.values().associate {
                it.name.lowercase(Locale.getDefault()) to value.level(it)
            }
        )
        encodeSerializableElement(
            descriptor,
            1,
            MapSerializer(String.serializer(), Double.serializer()),
            Skill.values().associate {
                it.name.lowercase(Locale.getDefault()) to value.xp(it)
            }
        )
    }
}
