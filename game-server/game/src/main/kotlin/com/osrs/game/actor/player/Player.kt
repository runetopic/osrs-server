package com.osrs.game.actor.player

import com.osrs.common.item.FloorItem
import com.osrs.common.map.location.Location
import com.osrs.common.map.location.ZoneLocation
import com.osrs.common.skill.Skill
import com.osrs.database.entity.Account
import com.osrs.game.actor.Actor
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.actor.movement.MovementQueue
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.actor.render.type.MovementType
import com.osrs.game.actor.render.type.MovementType.WALK
import com.osrs.game.container.Inventory
import com.osrs.game.hint.HintArrow.LOCATION
import com.osrs.game.network.Session
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.PacketGroup
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.network.packet.type.server.ClientScriptPacket
import com.osrs.game.network.packet.type.server.HintArrowPacket
import com.osrs.game.network.packet.type.server.MessageGamePacket
import com.osrs.game.network.packet.type.server.MidiSongPacket
import com.osrs.game.network.packet.type.server.PlayerInfoPacket
import com.osrs.game.network.packet.type.server.RebuildNormalPacket
import com.osrs.game.network.packet.type.server.SetPlayerOptionPacket
import com.osrs.game.network.packet.type.server.UpdateRunEnergyPacket
import com.osrs.game.network.packet.type.server.UpdateStatPacket
import com.osrs.game.network.packet.type.server.UpdateZoneFullFollowsPacket
import com.osrs.game.network.packet.type.server.VarpSmallPacket
import com.osrs.game.ui.Interfaces
import com.osrs.game.world.World
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Player(
    val account: Account,
    override var world: World,
    var session: Session,
) : Actor() {
    val username get() = account.userName
    val displayName get() = account.displayName
    val skills get() = account.skills

    var appearance = Appearance(Appearance.Gender.MALE, -1, -1, -1, false)

    lateinit var interfaces: Interfaces
    lateinit var inventory: Inventory
    var baseZoneLocation: ZoneLocation = Location.None.zoneLocation

    var lastLoadedLocation: Location = Location.None

    val movementQueue = MovementQueue(this)

    override var location: Location = Location.None
    override var zone = world.zone(location)
    override var moveDirection: MoveDirection? = null

    var objs = ArrayList<FloorItem>()

    var online = false

    var rights = 0

    private val viewport = Viewport(this)

    private val packetGroup = ConcurrentHashMap<Int, ArrayBlockingQueue<PacketGroup>>()

    fun initialize(
        interfaces: Interfaces,
        inventory: Inventory,
    ) {
        this.session.player = this
        this.location = account.location
        this.lastLocation = location
        this.interfaces = interfaces
        this.inventory = inventory
        this.objs += account.objs
        renderer.update(if (isRunning) MovementSpeed(MovementType.RUN) else MovementSpeed(WALK))
    }

    fun login() {
        session.writeLoginResponse()
        loadMapRegion(true)
        refreshAppearance()
        session.write(MessageGamePacket(0, "Welcome to Old School RuneScape.", false))
        updateStats()
        updateRunEnergy(runEnergy.toInt())
        session.write(VarpSmallPacket(1737, -1)) // TODO temporary working on a vars system atm.
        session.write(MidiSongPacket(62))
        session.write(SetPlayerOptionPacket("Follow", 1))
        session.write(SetPlayerOptionPacket("Trade", 2))
        session.write(
            HintArrowPacket(
                type = LOCATION,
                targetX = location.x,
                targetZ = location.z,
                targetHeight = 0,
            ),
        )

        val scripts = arrayOf(
            ClientScriptPacket(id = 5224, arrayOf(3)), // Combat level,
            ClientScriptPacket(2498, arrayOf(0, 0, 0)),
        )

        scripts.forEach(session::write)
        online = true
    }

    private fun loadMapRegion(initialize: Boolean) {
        lastLoadedLocation = location.clone()
        session.write(
            RebuildNormalPacket(
                viewport,
                location,
                initialize,
            ),
        )
        baseZoneLocation = ZoneLocation(x = location.zoneX - 6, z = location.zoneZ - 6)
        updateZones()
    }

    private fun updateZones() {
        zone.leaveZone(this)
        zone = world.zone(location)
        zone.enterZone(this)

        val baseZoneX = baseZoneLocation.x
        val baseZoneZ = baseZoneLocation.z
        val rangeX = max(baseZoneX, location.zoneX - 3)..min(baseZoneX + 11, location.zoneX + 3)
        val rangeZ = max(baseZoneZ, location.zoneZ - 3)..min(baseZoneZ + 11, location.zoneZ + 3)
        val existing = zones.toMutableSet()

        zones.clear()

        for (x in rangeX) {
            for (z in rangeZ) {
                val zoneLocation = ZoneLocation(x, z, location.level)
                val zonePackedLocation = zoneLocation.packedLocation

                zones += zonePackedLocation

                if (!existing.contains(zonePackedLocation)) {
                    val location = ZoneLocation(zonePackedLocation)
                    val xInScene = (location.x - baseZoneX) shl 3
                    val yInScene = (location.z - baseZoneZ) shl 3
                    session.write(UpdateZoneFullFollowsPacket(xInScene, yInScene))
                    world.zone(location).writeInitialZoneUpdates(this)
                }
            }
        }
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

        if (shouldUpdateZones()) {
            updateZones()
        }
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
        if (lastLoadedLocation == location) return false

        val lastZoneX = lastLoadedLocation.zoneX
        val lastZoneZ = lastLoadedLocation.zoneZ
        val zoneX = location.zoneX
        val zoneZ = location.zoneZ
        val limit = ((buildArea shr 3) / 2) - 1
        return abs(lastZoneX - zoneX) >= limit || abs(lastZoneZ - zoneZ) >= limit
    }

    private fun shouldUpdateZones() = zone.location.id != location.zoneId

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
            lowDefinitionUpdates = playerUpdateBlocks.lowDefinitionUpdates,
        ),
    )

    fun refreshAppearance(appearance: Appearance = this.appearance): Appearance {
        this.appearance = renderer.update(appearance)
        return this.appearance
    }

    fun message(string: String) {
        session.write(MessageGamePacket(0, string, false))
    }

    fun logout() {
        online = false
        zone.leaveZone(this)
    }

    fun write(packet: Packet) {
        session.write(packet)
    }
}
