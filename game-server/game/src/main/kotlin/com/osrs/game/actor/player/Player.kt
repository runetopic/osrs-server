package com.osrs.game.actor.player

import com.osrs.common.map.location.Location
import com.osrs.common.skill.Skill
import com.osrs.common.skill.Skills
import com.osrs.game.actor.Actor
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.actor.movement.MovementQueue
import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeedType
import com.osrs.game.container.Inventory
import com.osrs.game.network.Session
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.PacketGroup
import com.osrs.game.network.packet.builder.impl.sync.PlayerUpdateBlocks
import com.osrs.game.network.packet.type.server.MessageGamePacket
import com.osrs.game.network.packet.type.server.PlayerInfoPacket
import com.osrs.game.network.packet.type.server.RebuildNormalPacket
import com.osrs.game.network.packet.type.server.UpdateRunEnergyPacket
import com.osrs.game.network.packet.type.server.UpdateStatPacket
import com.osrs.game.network.packet.type.server.VarpSmallPacket
import com.osrs.game.ui.InterfaceLayout.RESIZABLE
import com.osrs.game.ui.Interfaces
import com.osrs.game.world.World
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

class Player(
    override var location: Location = Location.None,
    val username: String,
    override var world: World,
    val skills: Skills,
    var session: Session,
) : Actor() {
    var appearance = Appearance(Appearance.Gender.MALE, -1, -1, -1, false)

    lateinit var interfaces: Interfaces
    lateinit var inventory: Inventory

    var lastLoadedLocation: Location? = null

    val movementQueue = MovementQueue(this)

    override var moveDirection: MoveDirection? = null

    var online = false

    var rights = 0

    private val viewport = Viewport(this)

    private val packetGroup = ConcurrentHashMap<Int, ArrayBlockingQueue<PacketGroup>>()

    fun initialize(
        interfaces: Interfaces,
        inventory: Inventory
    ) {
        this.session.player = this
        this.lastLocation = location
        this.interfaces = interfaces
        this.inventory = inventory
        renderer.updateMovementSpeed(if (isRunning) MovementSpeedType.RUN else MovementSpeedType.WALK)
    }

    fun login() {
        session.writeLoginResponse()
        loadMapRegion(true)
        refreshAppearance()
        updateStats()
        updateRunEnergy(runEnergy.toInt())
        session.write(VarpSmallPacket(1737, -1)) // TODO temporary working on a vars system atm.
        session.write(MessageGamePacket(0, "Welcome to Old School RuneScape.", false))
        inventory.sendInventory()
        this.interfaces.sendInterfaceLayout(RESIZABLE)
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

    fun process() {
        movementQueue.process()

        if (shouldRebuildMap()) loadMapRegion(false)
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

    private fun shouldRebuildMap(buildArea: Int = 104): Boolean {
        if (lastLoadedLocation == null || lastLoadedLocation == location) return false

        val lastZoneX = lastLoadedLocation!!.zoneX
        val lastZoneZ = lastLoadedLocation!!.zoneZ
        val zoneX = location.zoneX
        val zoneZ = location.zoneZ
        val limit = ((buildArea shr 3) / 2) - 1
        return abs(lastZoneX - zoneX) >= limit || abs(lastZoneZ - zoneZ) >= limit
    }

    private fun updateStats() {
        Skill.values().forEach {
            val level = skills.level(it)
            val experience = skills.xp(it)
            updateStat(it, level, experience)
        }
    }

    fun updateStat(skill: Skill, level: Int, experience: Double) = write(UpdateStatPacket(skill.id, level, experience))

    fun updateRunEnergy(energy: Int) = write(UpdateRunEnergyPacket(energy))

    fun sendPlayerInfo(playerUpdateBlocks: PlayerUpdateBlocks) = session.write(
        PlayerInfoPacket(
            viewport = viewport,
            players = world.players,
            highDefinitionUpdates = playerUpdateBlocks.highDefinitionUpdates,
            lowDefinitionUpdates = playerUpdateBlocks.lowDefinitionUpdates
        )
    )

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
