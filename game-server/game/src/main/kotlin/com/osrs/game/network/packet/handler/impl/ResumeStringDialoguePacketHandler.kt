package com.osrs.game.network.packet.handler.impl

import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.ResumeStringDialoguePacket

class ResumeStringDialoguePacketHandler : PacketHandler<ResumeStringDialoguePacket>() {
    override fun handlePacket(packet: ResumeStringDialoguePacket, player: Player) {
        println("Received input: ${packet.input}")
    }
}
