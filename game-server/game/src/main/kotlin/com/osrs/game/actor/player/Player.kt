package com.osrs.game.actor.player

import com.osrs.api.item.FloorItem
import com.osrs.api.skill.Skill
import com.osrs.database.entity.Account
import com.osrs.game.actor.Actor
import com.osrs.game.actor.movement.MovementType
import com.osrs.game.actor.movement.MovementType.WALK
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.Gender
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.container.Inventory
import com.osrs.game.network.Session
import com.osrs.game.network.packet.PacketGroup
import com.osrs.game.network.packet.type.server.RebuildNormalPacket
import com.osrs.game.ui.Interfaces
import com.osrs.game.ui.UserInterface.SetDisplayName
import com.osrs.game.ui.UserInterface.TutorialIslandProgress
import com.osrs.game.world.World
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap

class Player(
    val account: Account,
    world: World,
    val session: Session
) : Actor(world) {
    // Late initialized properties.
    lateinit var interfaces: Interfaces
        private set
    lateinit var inventory: Inventory
        private set

    // Immutable properties.
    val username get() = account.userName
    val displayName get() = account.displayName
    val skills get() = account.skills
    val objs = ArrayList<FloorItem>()
    val viewport = Viewport(this)
    val packetGroup = ConcurrentHashMap<Int, ArrayBlockingQueue<PacketGroup>>()

    // Mutable properties.
    var rights = 0
    var appearance = Appearance(Gender.MALE, -1, -1, -1, false, displayName)

    private var hasCompletedTutorial = false

    fun initialize(
        interfaces: Interfaces,
        inventory: Inventory
    ) {
        super.initialize(account.location)
        this.session.player = this
        this.rights = account.rights
        this.interfaces = interfaces
        this.inventory = inventory
        this.objs += account.objs
    }

    override fun login() {
        session.writeLoginResponse()
        renderer.update(if (isRunning) MovementSpeed(MovementType.RUN) else MovementSpeed(WALK))
        updateMap(true)
        refreshAppearance()
        updateStats()
        updateRunEnergy(runEnergy.toInt())

        if (!hasCompletedTutorial) {
            // TODO extract this all out into a system
            interfaces += TutorialIslandProgress
            interfaces += SetDisplayName
        }

        online = true
    }

    override fun logout() {
        zone.leaveZone(this)
        online = false
    }

    override fun totalHitpoints(): Int = 100

    override fun currentHitpoints(): Int = 100
    override fun processMovement() {
        movementQueue.process(this)

        if (lastLocation != location) {
            world.collisionMap.removeActorCollision(lastLocation)
            world.collisionMap.addActorCollision(location)
        }
    }

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

    private fun updateStats() = enumValues<Skill>().forEach { skill ->
        val level = skills.level(skill)
        val experience = skills.xp(skill)
        updateStat(skill, level, experience)
    }
}
