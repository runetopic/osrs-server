package com.osrs.game.actor.player

import com.osrs.common.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.actor.render.RenderBlock
import com.osrs.game.actor.render.impl.PlayerRenderer
import com.osrs.game.network.Session
import com.osrs.game.network.packet.server.IfOpenTopPacket
import com.osrs.game.network.packet.server.RebuildNormalPacket
import com.osrs.game.world.World
import java.util.Random

class Player(
    val username: String,
    var world: World,
    var session: Session
) : Actor() {
    override val renderer = PlayerRenderer()
    override var location: Location = Location(3222 + Random().nextInt(2), 3222 + Random().nextInt(2))
    var appearance = RenderBlock.Appearance(RenderBlock.Appearance.Gender.MALE, -1, -1, -1, false)
    val viewport = Viewport(this)
    var online = false
    var rights = 0

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
        online = true
    }

    fun logout(world: World) {
        world.players.remove(this)
    }

    fun refreshAppearance(appearance: RenderBlock.Appearance = this.appearance): RenderBlock.Appearance {
        this.appearance = renderer.refreshAppearance(appearance)
        return this.appearance
    }

    fun writeAndFlush() {
        session.invokeAndClearWritePool()
    }
}
