package com.osrs.game.world

import com.osrs.cache.Cache
import com.osrs.cache.entry.config.map.MapSquareEntryProvider
import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.Player
import com.osrs.game.network.Session
import com.osrs.game.world.map.CollisionMap
import java.util.concurrent.ConcurrentHashMap

data class World(
    val worldId: Int,
    val cache: Cache,
    val maps: MapSquareEntryProvider,
    val collisionMap: CollisionMap
) {
    val players: PlayerList = PlayerList(MAX_PLAYERS)

    private val loginRequests = ConcurrentHashMap<Player, Session>()

    var isOnline = false

    fun start() {
        isOnline = true
        maps.forEach(collisionMap::applyCollision)
    }

    fun processLoginRequest() {
        if (!isOnline) return

        loginRequests.entries.take(150).onEach {
            players.add(it.key)
            it.key.login(this)
        }.also(loginRequests.entries::removeAll)
    }

    fun requestLogin(session: Session, player: Player) {
        this.loginRequests[player] = session
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}
