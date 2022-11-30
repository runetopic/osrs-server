package com.osrs.database.account.serializer

import com.osrs.game.world.map.Location
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

class LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("location") {
            element<Int>("x")
            element<Int>("z")
            element<Int>("level")
        }

    override fun deserialize(decoder: Decoder): Location = decoder.decodeStructure(descriptor) {
        val x = decodeIntElement(descriptor, decodeElementIndex(descriptor))
        val z = decodeIntElement(descriptor, decodeElementIndex(descriptor))
        val level = decodeIntElement(descriptor, decodeElementIndex(descriptor))
        return@decodeStructure Location(x, z, level)
    }

    override fun serialize(encoder: Encoder, value: Location) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.x)
        encodeIntElement(descriptor, 1, value.z)
        encodeIntElement(descriptor, 2, value.level)
    }
}
