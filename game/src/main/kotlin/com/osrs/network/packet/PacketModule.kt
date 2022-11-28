package com.osrs.network.packet

import com.google.inject.Singleton
import com.osrs.network.packet.builder.PacketBuilder
import com.osrs.network.packet.builder.impl.IfOpenTopPacketBuilder
import com.osrs.network.packet.builder.impl.RebuildNormalPacketBuilder
import com.osrs.network.packet.server.IfOpenTopPacket
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMapBinder
import kotlin.reflect.KClass

@Singleton
object PacketModule : KotlinModule() {
    override fun configure() {
        val builders = KotlinMapBinder.newMapBinder<KClass<*>, PacketBuilder<Packet>>(kotlinBinder)
        builders.addBinding(RebuildNormalPacket::class).to<RebuildNormalPacketBuilder>().asEagerSingleton()
        builders.addBinding(IfOpenTopPacket::class).to<IfOpenTopPacketBuilder>().asEagerSingleton()
    }
}
