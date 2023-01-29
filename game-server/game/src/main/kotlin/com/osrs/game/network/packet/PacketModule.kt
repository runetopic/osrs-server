package com.osrs.game.network.packet

import com.google.inject.Singleton
import com.osrs.game.network.packet.client.IdlePacket
import com.osrs.game.network.packet.client.MovementPacket
import com.osrs.game.network.packet.client.WindowStatusPacket
import com.osrs.game.network.packet.client.handler.PacketHandler
import com.osrs.game.network.packet.client.handler.impl.IdlePacketHandler
import com.osrs.game.network.packet.client.handler.impl.MovementPacketHandler
import com.osrs.game.network.packet.client.handler.impl.WindowStatusPacketHandler
import com.osrs.game.network.packet.client.reader.PacketReader
import com.osrs.game.network.packet.client.reader.impl.IdlePacketReader
import com.osrs.game.network.packet.client.reader.impl.MovementPacketReader
import com.osrs.game.network.packet.client.reader.impl.WindowStatusPacketReader
import com.osrs.game.network.packet.server.IfOpenSubPacket
import com.osrs.game.network.packet.server.IfOpenTopPacket
import com.osrs.game.network.packet.server.PlayerInfoPacket
import com.osrs.game.network.packet.server.RebuildNormalPacket
import com.osrs.game.network.packet.server.builder.PacketBuilder
import com.osrs.game.network.packet.server.builder.impl.IfOpenSubPacketBuilder
import com.osrs.game.network.packet.server.builder.impl.IfOpenTopPacketBuilder
import com.osrs.game.network.packet.server.builder.impl.PlayerInfoPacketBuilder
import com.osrs.game.network.packet.server.builder.impl.RebuildNormalPacketBuilder
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMapBinder
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import kotlin.reflect.KClass

@Singleton
object PacketModule : KotlinModule() {
    override fun configure() {
        val builders = KotlinMapBinder.newMapBinder<KClass<*>, PacketBuilder<Packet>>(kotlinBinder)

        builders.addBinding(RebuildNormalPacket::class).to<RebuildNormalPacketBuilder>().asEagerSingleton()
        builders.addBinding(IfOpenTopPacket::class).to<IfOpenTopPacketBuilder>().asEagerSingleton()
        builders.addBinding(PlayerInfoPacket::class).to<PlayerInfoPacketBuilder>().asEagerSingleton()
        builders.addBinding(IfOpenSubPacket::class).to<IfOpenSubPacketBuilder>().asEagerSingleton()

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
