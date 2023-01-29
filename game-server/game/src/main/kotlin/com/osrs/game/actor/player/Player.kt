package com.osrs.game.actor.player

import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.actor.MoveDirection
import com.osrs.game.actor.movement.MovementQueue
import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeedType
import com.osrs.game.network.Session
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.PacketGroup
import com.osrs.game.network.packet.server.PlayerInfoPacket
import com.osrs.game.network.packet.server.RebuildNormalPacket
import com.osrs.game.tick.task.player.PlayerUpdateBlocks
import com.osrs.game.ui.Interfaces
import com.osrs.game.world.World
import java.util.concurrent.ArrayBlockingQueue
import kotlin.math.abs

class Player(
    val username: String,
    override var world: World,
    var session: Session
) : Actor() {
    override var moveDirection: MoveDirection? = null
    override var location: Location = Location(3162, 3490)
    var appearance = Appearance(Appearance.Gender.MALE, -1, -1, -1, false)
    val movementQueue = MovementQueue(this)
    val viewport = Viewport(this)
    val interfaces = Interfaces()
    var online = false
    var rights = 0
    var lastLoadedLocation: Location? = null

    private val packetGroup = mutableMapOf<Int, ArrayBlockingQueue<PacketGroup>>()

    fun login() {
        this.session.player = this
        this.lastLocation = location
        session.writeLoginResponse()
        loadMapRegion(true)
        renderer.updateMovementSpeed(if (isRunning) MovementSpeedType.RUN else MovementSpeedType.WALK)
        refreshAppearance()
        online = true
    }

    private fun loadMapRegion(initialize: Boolean) {
        session.write(
            RebuildNormalPacket(
                viewport,
                location,
                initialize
            )
        )
        lastLoadedLocation = location
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

        if (shouldRebuildMap()) loadMapRegion(false)
    }

    private fun shouldRebuildMap(buildArea: Int = 104): Boolean {
        if (lastLoadedLocation == null || lastLoadedLocation == location) return false

        val lastZoneX = lastLoadedLocation!!.zoneX
        val lastZoneZ = lastLoadedLocation!!.zoneZ
        val zoneX = location.zoneX
        val zoneZ = location.zoneZ
        val limit = ((buildArea shr 3) / 2) - 1
        return abs(lastZoneX - zoneX) >= limit || abs(lastZoneZ - zoneZ) >= limit
    }

    fun sendPlayerInfo() = session.write(PlayerInfoPacket(viewport, world.players, PlayerUpdateBlocks.pendingUpdateBlocks()))

    fun refreshAppearance(appearance: Appearance = this.appearance): Appearance {
        this.appearance = renderer.appearance(appearance)
        return this.appearance
    }

    fun logout() {
        online = false
    }

    fun write(packet: Packet) {
        session.write(packet)
    }
}
