package com.osrs.game.network.packet.type.server

import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.builder.PacketBuilder
import kotlin.reflect.KClass

data class UpdateZonePartialEnclosedPacket(
    val xInScene: Int,
    val zInScene: Int,
    val zoneUpdates: Sequence<Packet>?,
    val builders: Map<KClass<*>, PacketBuilder<Packet>>,
) : Packet
