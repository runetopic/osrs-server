package com.osrs.game.network.packet.client.handler.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.client.MovementPacket
import com.osrs.game.network.packet.client.handler.PacketHandler
import org.rsmod.pathfinder.PathFinder

@Singleton
class MovementPacketHandler @Inject constructor(
    private val pathFinder: PathFinder
) : PacketHandler<MovementPacket>(
    groupId = 1
) {
    override fun handlePacket(packet: MovementPacket, player: Player) {
        val path = pathFinder.findPath(
            srcX = player.location.x,
            srcY = player.location.z,
            destX = packet.destinationX,
            destY = packet.destinationZ,
            z = player.location.level
        )

        println(path.toString())
        player.movementQueue.appendRoute(path)
    }
}
