package com.osrs.game.actor.player

import com.osrs.common.item.FloorItem
import com.osrs.common.skill.Skill
import com.osrs.database.entity.Account
import com.osrs.game.actor.Actor
import com.osrs.game.actor.movement.MovementQueue
import com.osrs.game.actor.movement.MovementType
import com.osrs.game.actor.movement.MovementType.WALK
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.container.Inventory
import com.osrs.game.hint.HintArrow
import com.osrs.game.network.Session
import com.osrs.game.network.packet.PacketGroup
import com.osrs.game.network.packet.type.server.ClientScriptPacket
import com.osrs.game.network.packet.type.server.HintArrowPacket
import com.osrs.game.network.packet.type.server.MessageGamePacket
import com.osrs.game.network.packet.type.server.MidiSongPacket
import com.osrs.game.network.packet.type.server.RebuildNormalPacket
import com.osrs.game.network.packet.type.server.SetPlayerOptionPacket
import com.osrs.game.network.packet.type.server.VarpSmallPacket
import com.osrs.game.ui.Interfaces
import com.osrs.game.world.World
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap

class Player(
    val account: Account,
    world: World,
    val session: Session
) : Actor(world) {
    // Late initialized properties.
    var interfaces: Interfaces? = null
    var inventory: Inventory? = null

    // Immutable properties.
    val username get() = account.userName
    val displayName get() = account.displayName
    val skills get() = account.skills
    val objs = ArrayList<FloorItem>()
    val viewport = Viewport(this)
    val packetGroup = ConcurrentHashMap<Int, ArrayBlockingQueue<PacketGroup>>()

    // Mutable properties.
    var rights = 0
    var appearance = Appearance(Appearance.Gender.MALE, -1, -1, -1, false, displayName)

    fun initialize(
        interfaces: Interfaces,
        inventory: Inventory
    ) {
        super.initialize()
        this.session.player = this
        this.location = account.location
        this.rights = account.rights
        this.lastLocation = location
        this.interfaces = interfaces
        this.inventory = inventory
        this.objs += account.objs
        this.movementQueue = MovementQueue(this)
    }

    override fun login() {
        session.writeLoginResponse()
        renderer.update(if (isRunning) MovementSpeed(MovementType.RUN) else MovementSpeed(WALK))
        updateMap(true)
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
                type = HintArrow.LOCATION,
                targetX = location.x,
                targetZ = location.z,
                targetHeight = 0
            )
        )

        val scripts = arrayOf(
            ClientScriptPacket(id = 5224, arrayOf(3)), // Combat level,
            ClientScriptPacket(2498, arrayOf(0, 0, 0))
        )

        scripts.forEach(session::write)
        online = true
    }

    override fun logout() {
        zone.leaveZone(this)
        online = false
    }

    override fun totalHitpoints(): Int = 100

    override fun currentHitpoints(): Int = 100

    override fun updateMap(initialize: Boolean) {
        super.updateMap(initialize)
        session.write(
            RebuildNormalPacket(
                viewport,
                location,
                initialize
            )
        )
    }

    private fun updateStats() {
        Skill.values().forEach {
            val level = skills.level(it)
            val experience = skills.xp(it)
            updateStat(it, level, experience)
        }
    }
}
