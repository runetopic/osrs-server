package com.osrs.game.network.packet.type.client

import com.osrs.game.network.packet.Packet

data class ResumeStringDialoguePacket(
    val input: String
) : Packet
