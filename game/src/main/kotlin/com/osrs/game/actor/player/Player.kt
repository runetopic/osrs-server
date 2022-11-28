package com.osrs.game.actor.player

import com.osrs.game.actor.Actor
import com.osrs.game.world.World
import com.osrs.game.world.map.Location
import com.osrs.network.Session
import com.osrs.network.packet.RebuildNormalPacket
import com.osrs.network.packet.server.IfOpenTopPacket

class Player(
    val username: String,
    override var location: Location = Location(3222, 3222)
) : Actor() {

    var session: Session? = null

    private val viewport = Viewport(this)

    fun login(session: Session, world: World) {
        this.session = session
        this.session?.player = this
        session.write(
            RebuildNormalPacket(
                viewport,
                location,
                true,
                world.players
            )
        )
        session.write(
            IfOpenTopPacket(
                548
            )
        )
        session.invokeAndClearWritePool()
    }

    fun logout(world: World) {
        world.players.remove(this)
    }
}
