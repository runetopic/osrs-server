package com.osrs.game.network.packet

import com.osrs.game.network.packet.reader.impl.MoveMiniMapPacketReader
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.builder.impl.CameraResetPacketBuilder
import com.osrs.game.network.packet.builder.impl.HintArrowPacketBuilder
import com.osrs.game.network.packet.builder.impl.IfOpenSubPacketBuilder
import com.osrs.game.network.packet.builder.impl.IfOpenTopPacketBuilder
import com.osrs.game.network.packet.builder.impl.MessageGamePacketBuilder
import com.osrs.game.network.packet.builder.impl.MidiSongPacketBuilder
import com.osrs.game.network.packet.builder.impl.ObjAddPacketBuilder
import com.osrs.game.network.packet.builder.impl.PlayerInfoPacketBuilder
import com.osrs.game.network.packet.builder.impl.RebuildNormalPacketBuilder
import com.osrs.game.network.packet.builder.impl.SetPlayerOptionPacketBuilder
import com.osrs.game.network.packet.builder.impl.UpdateContainerFullPacketBuilder
import com.osrs.game.network.packet.builder.impl.UpdateZoneFullFollowsPacketBuilder
import com.osrs.game.network.packet.builder.impl.UpdateRunEnergyPacketBuilder
import com.osrs.game.network.packet.builder.impl.UpdateStatPacketBuilder
import com.osrs.game.network.packet.builder.impl.UpdateZonePartialEnclosedPacketBuilder
import com.osrs.game.network.packet.builder.impl.UpdateZonePartialFollowsPacketBuilder
import com.osrs.game.network.packet.builder.impl.VarpLargePacketBuilder
import com.osrs.game.network.packet.builder.impl.VarpSmallPacketBuilder
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.handler.impl.IdlePacketHandler
import com.osrs.game.network.packet.handler.impl.IfButtonPacketHandler
import com.osrs.game.network.packet.handler.impl.MoveGamePacketHandler
import com.osrs.game.network.packet.handler.impl.MoveMiniMapPacketHandler
import com.osrs.game.network.packet.handler.impl.NoTimeoutPacketHandler
import com.osrs.game.network.packet.handler.impl.WindowStatusPacketHandler
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.reader.impl.IdlePacketReader
import com.osrs.game.network.packet.reader.impl.IfButton10PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton1PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton2PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton3PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton4PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton5PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton6PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton7PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton8PacketReader
import com.osrs.game.network.packet.reader.impl.IfButton9PacketReader
import com.osrs.game.network.packet.reader.impl.MoveGamePacketReader
import com.osrs.game.network.packet.reader.impl.NoTimeoutPacketReader
import com.osrs.game.network.packet.reader.impl.WindowStatusPacketReader
import com.osrs.game.network.packet.type.client.IdlePacket
import com.osrs.game.network.packet.type.client.IfButtonPacket
import com.osrs.game.network.packet.type.client.MoveGamePacket
import com.osrs.game.network.packet.type.client.MoveMiniMapPacket
import com.osrs.game.network.packet.type.client.NoTimeoutPacket
import com.osrs.game.network.packet.type.client.WindowStatusPacket
import com.osrs.game.network.packet.type.server.CameraReset
import com.osrs.game.network.packet.type.server.HintArrowPacket
import com.osrs.game.network.packet.type.server.IfOpenSubPacket
import com.osrs.game.network.packet.type.server.IfOpenTopPacket
import com.osrs.game.network.packet.type.server.MessageGamePacket
import com.osrs.game.network.packet.type.server.MidiSongPacket
import com.osrs.game.network.packet.type.server.ObjAddPacket
import com.osrs.game.network.packet.type.server.PlayerInfoPacket
import com.osrs.game.network.packet.type.server.RebuildNormalPacket
import com.osrs.game.network.packet.type.server.SetPlayerOptionPacket
import com.osrs.game.network.packet.type.server.UpdateContainerFullPacket
import com.osrs.game.network.packet.type.server.UpdateRunEnergyPacket
import com.osrs.game.network.packet.type.server.UpdateStatPacket
import com.osrs.game.network.packet.type.server.UpdateZoneFullFollowsPacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialEnclosedPacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialFollowsPacket
import com.osrs.game.network.packet.type.server.VarpLargePacket
import com.osrs.game.network.packet.type.server.VarpSmallPacket
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMapBinder
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import kotlin.reflect.KClass

object PacketModule : KotlinModule() {
    override fun configure() {
        val builders = KotlinMapBinder.newMapBinder<KClass<*>, PacketBuilder<Packet>>(kotlinBinder)

        builders.addBinding(RebuildNormalPacket::class).to<RebuildNormalPacketBuilder>().asEagerSingleton()
        builders.addBinding(IfOpenTopPacket::class).to<IfOpenTopPacketBuilder>().asEagerSingleton()
        builders.addBinding(PlayerInfoPacket::class).to<PlayerInfoPacketBuilder>().asEagerSingleton()
        builders.addBinding(IfOpenSubPacket::class).to<IfOpenSubPacketBuilder>().asEagerSingleton()
        builders.addBinding(VarpLargePacket::class).to<VarpLargePacketBuilder>().asEagerSingleton()
        builders.addBinding(VarpSmallPacket::class).to<VarpSmallPacketBuilder>().asEagerSingleton()
        builders.addBinding(MessageGamePacket::class).to<MessageGamePacketBuilder>().asEagerSingleton()
        builders.addBinding(UpdateStatPacket::class).to<UpdateStatPacketBuilder>().asEagerSingleton()
        builders.addBinding(UpdateRunEnergyPacket::class).to<UpdateRunEnergyPacketBuilder>().asEagerSingleton()
        builders.addBinding(UpdateContainerFullPacket::class).to<UpdateContainerFullPacketBuilder>().asEagerSingleton()
        builders.addBinding(CameraReset::class).to<CameraResetPacketBuilder>().asEagerSingleton()
        builders.addBinding(MidiSongPacket::class).to<MidiSongPacketBuilder>().asEagerSingleton()
        builders.addBinding(HintArrowPacket::class).to<HintArrowPacketBuilder>().asEagerSingleton()
        builders.addBinding(SetPlayerOptionPacket::class).to<SetPlayerOptionPacketBuilder>().asEagerSingleton()
        builders.addBinding(UpdateZoneFullFollowsPacket::class).to<UpdateZoneFullFollowsPacketBuilder>().asEagerSingleton()
        builders.addBinding(UpdateZonePartialFollowsPacket::class).to<UpdateZonePartialFollowsPacketBuilder>().asEagerSingleton()
        builders.addBinding(UpdateZonePartialEnclosedPacket::class).to<UpdateZonePartialEnclosedPacketBuilder>().asEagerSingleton()
        builders.addBinding(ObjAddPacket::class).to<ObjAddPacketBuilder>().asEagerSingleton()

        val readers = KotlinMultibinder.newSetBinder<PacketReader<Packet>>(kotlinBinder)

        readers.addBinding().to<MoveGamePacketReader>().asEagerSingleton()
        readers.addBinding().to<MoveMiniMapPacketReader>().asEagerSingleton()
        readers.addBinding().to<IdlePacketReader>().asEagerSingleton()
        readers.addBinding().to<NoTimeoutPacketReader>().asEagerSingleton()
        readers.addBinding().to<WindowStatusPacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton1PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton2PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton3PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton4PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton5PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton6PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton7PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton8PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton9PacketReader>().asEagerSingleton()
        readers.addBinding().to<IfButton10PacketReader>().asEagerSingleton()

        val handlers = KotlinMapBinder.newMapBinder<KClass<*>, PacketHandler<Packet>>(kotlinBinder)

        handlers.addBinding(IfButtonPacket::class).to<IfButtonPacketHandler>().asEagerSingleton()
        handlers.addBinding(MoveGamePacket::class).to<MoveGamePacketHandler>().asEagerSingleton()
        handlers.addBinding(MoveMiniMapPacket::class).to<MoveMiniMapPacketHandler>().asEagerSingleton()
        handlers.addBinding(IdlePacket::class).to<IdlePacketHandler>().asEagerSingleton()
        handlers.addBinding(WindowStatusPacket::class).to<WindowStatusPacketHandler>().asEagerSingleton()
        handlers.addBinding(NoTimeoutPacket::class).to<NoTimeoutPacketHandler>().asEagerSingleton()
    }
}
