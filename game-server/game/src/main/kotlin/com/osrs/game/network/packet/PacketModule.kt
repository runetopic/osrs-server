package com.osrs.game.network.packet

import com.google.inject.Singleton
import com.osrs.game.network.packet.builder.impl.RebuildNormalPacketBuilder
import com.osrs.game.network.packet.handler.impl.WindowStatusPacketHandler
import com.osrs.game.network.packet.type.client.IdlePacket
import com.osrs.game.network.packet.type.client.MovementPacket
import com.osrs.game.network.packet.type.client.WindowStatusPacket
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.handler.impl.IdlePacketHandler
import com.osrs.game.network.packet.handler.impl.MovementPacketHandler
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.reader.impl.IdlePacketReader
import com.osrs.game.network.packet.reader.impl.MovementPacketReader
import com.osrs.game.network.packet.reader.impl.WindowStatusPacketReader
import com.osrs.game.network.packet.type.server.IfOpenSubPacket
import com.osrs.game.network.packet.type.server.IfOpenTopPacket
import com.osrs.game.network.packet.type.server.PlayerInfoPacket
import com.osrs.game.network.packet.type.server.RebuildNormalPacket
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMapBinder
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import kotlin.reflect.KClass

@Singleton
object PacketModule : KotlinModule() {
    override fun configure() {
        val builders = KotlinMapBinder.newMapBinder<KClass<*>, com.osrs.game.network.packet.builder.PacketBuilder<Packet>>(kotlinBinder)

        builders.addBinding(RebuildNormalPacket::class).to<RebuildNormalPacketBuilder>().asEagerSingleton()
        builders.addBinding(IfOpenTopPacket::class).to<com.osrs.game.network.packet.builder.impl.IfOpenTopPacketBuilder>().asEagerSingleton()
        builders.addBinding(PlayerInfoPacket::class).to<com.osrs.game.network.packet.builder.impl.PlayerInfoPacketBuilder>().asEagerSingleton()
        builders.addBinding(IfOpenSubPacket::class).to<com.osrs.game.network.packet.builder.impl.IfOpenSubPacketBuilder>().asEagerSingleton()

        val readers = KotlinMultibinder.newSetBinder<PacketReader<Packet>>(kotlinBinder)
        readers.addBinding().to<MovementPacketReader>().asEagerSingleton()
        readers.addBinding().to<IdlePacketReader>().asEagerSingleton()
        readers.addBinding().to<WindowStatusPacketReader>().asEagerSingleton()

        val handlers = KotlinMapBinder.newMapBinder<KClass<*>, PacketHandler<Packet>>(kotlinBinder)

        handlers.addBinding(MovementPacket::class).to<MovementPacketHandler>().asEagerSingleton()
        handlers.addBinding(IdlePacket::class).to<IdlePacketHandler>().asEagerSingleton()
        handlers.addBinding(WindowStatusPacket::class).to<WindowStatusPacketHandler>().asEagerSingleton()
    }
}
