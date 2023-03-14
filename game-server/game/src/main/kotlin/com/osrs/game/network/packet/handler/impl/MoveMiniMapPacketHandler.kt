package com.osrs.game.network.packet.handler.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.MoveMiniMapPacket
import org.rsmod.pathfinder.PathFinder

@Singleton
class MoveMiniMapPacketHandler @Inject constructor(
    private val pathFinder: PathFinder
) : PacketHandler<MoveMiniMapPacket>(
    groupId = 1
) {
    override fun handlePacket(packet: MoveMiniMapPacket, player: Player) {
        val path = pathFinder.findPath(
            srcX = player.location.x,
            srcY = player.location.z,
            destX = packet.destinationX,
            destY = packet.destinationZ,
            z = player.location.level
        )

        player.movementQueue.appendRoute(path)
    }
}
