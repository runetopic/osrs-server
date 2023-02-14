package com.osrs.game.network.packet.handler.impl

import com.google.inject.Singleton
import com.osrs.common.util.childId
import com.osrs.common.util.interfaceId
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.IfButtonPacket

@Singleton
class IfButtonPacketHandler : PacketHandler<IfButtonPacket>() {
    override fun handlePacket(packet: IfButtonPacket, player: Player) {
        println("Clicking button $packet")
        println(packet.packedInterface.interfaceId())
        println(packet.packedInterface.childId())
    }
}
