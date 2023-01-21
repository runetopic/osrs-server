package com.osrs.game.actor.player

import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.actor.MoveDirection
import com.osrs.game.actor.movement.MovementQueue
import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.network.Session
import com.osrs.game.network.packet.PacketGroup
import com.osrs.game.network.packet.server.IfOpenTopPacket
import com.osrs.game.network.packet.server.RebuildNormalPacket
import com.osrs.game.world.World
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class Player(
    val username: String,
    var world: World,
    var session: Session
) : Actor() {
    override var moveDirection: MoveDirection? = null
    override var location: Location = Location(3222 + Random().nextInt(2), 3222 + Random().nextInt(2))
    var appearance = Appearance(Appearance.Gender.MALE, -1, -1, -1, false)
    val viewport = Viewport(this)
    var online = false
    var rights = 0

    val movementQueue = MovementQueue(this)

    private val packetGroup = mutableMapOf<Int, ArrayBlockingQueue<PacketGroup>>()

    fun login(world: World) {
        this.session.player = this
        this.world = world
        this.lastLocation = location
        session.writeLoginResponse()
        session.write(
            RebuildNormalPacket(
                viewport,
                location,
                true
            )
        )
        session.write(
            IfOpenTopPacket(
                161
            )
        )
        refreshAppearance()
        renderer.temporaryMovementType()
        online = true
    }

    fun writeAndFlush() = session.invokeAndClearWritePool()

    fun addToPacketGroup(group: PacketGroup) {
        packetGroup
            .computeIfAbsent(group.handler.groupId) { ArrayBlockingQueue<PacketGroup>(10) }
            .offer(group)
    }

    fun processGroupedPackets() {
        for (handler in packetGroup) {
            val queue = handler.value

            for (i in 0 until 10) {
                val group = queue.poll() ?: break
                group.handler.handlePacket(group.packet, this)
            }

            queue.clear()
        }
    }

    fun process() {
        movementQueue.process()
    }

    fun refreshAppearance(appearance: Appearance = this.appearance): Appearance {
        this.appearance = renderer.appearance(appearance)
        return this.appearance
    }

    fun logout() {
        online = false
    }
}
