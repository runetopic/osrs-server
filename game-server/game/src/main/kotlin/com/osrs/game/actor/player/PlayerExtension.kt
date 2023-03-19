package com.osrs.game.actor.player

import com.osrs.common.skill.Skill
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.PacketGroup
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.network.packet.type.server.MessageGamePacket
import com.osrs.game.network.packet.type.server.PlayerInfoPacket
import com.osrs.game.network.packet.type.server.UpdateRunEnergyPacket
import com.osrs.game.network.packet.type.server.UpdateStatPacket
import java.util.concurrent.ArrayBlockingQueue

/**
 * @author Jordan Abraham
 */
fun Player.writeAndFlush() {
    session.invokeAndClearWritePool()
}

fun Player.addToPacketGroup(group: PacketGroup) {
    packetGroup
        .computeIfAbsent(group.handler.groupId) { ArrayBlockingQueue<PacketGroup>(10) }
        .offer(group)
}

fun Player.processGroupedPackets() {
    for (handler in packetGroup) {
        val queue = handler.value

        for (i in 0 until 10) {
            val group = queue.poll() ?: break
            group.handler.handlePacket(group.packet, this)
        }

        queue.clear()
    }
}

fun Player.updateStat(skill: Skill, level: Int, experience: Double) = write(UpdateStatPacket(skill.id, level, experience))

fun Player.updateRunEnergy(energy: Int) = write(UpdateRunEnergyPacket(energy))

fun Player.sendPlayerInfo(playerUpdateBlocks: PlayerUpdateBlocks) = session.write(
    PlayerInfoPacket(
        viewport = viewport,
        players = world.players,
        highDefinitionUpdates = playerUpdateBlocks.highDefinitionUpdates,
        lowDefinitionUpdates = playerUpdateBlocks.lowDefinitionUpdates
    )
)

fun Player.refreshAppearance(appearance: Appearance = this.appearance): Appearance {
    this.appearance = renderer.update(appearance)
    return this.appearance
}

fun Player.message(string: String) {
    session.write(MessageGamePacket(0, string, false))
}

fun Player.write(packet: Packet) {
    session.write(packet)
}
