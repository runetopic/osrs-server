package com.osrs.game.world

import com.osrs.cache.Cache
import com.osrs.cache.entry.map.MapSquareEntryTypeMap
import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.Player
import com.osrs.game.network.Session
import com.osrs.game.world.map.CollisionMap
import org.rsmod.pathfinder.StepValidator
import java.util.concurrent.ConcurrentHashMap

data class World(
    val worldId: Int,
    val cache: Cache,
    val maps: MapSquareEntryTypeMap,
    val collisionMap: CollisionMap,
    val stepValidator: StepValidator
) {
    val players: PlayerList = PlayerList(MAX_PLAYERS)

    private val loginRequests = ConcurrentHashMap<Player, Session>()
    private val logoutRequest = ConcurrentHashMap<Player, Session>()

    var isOnline = false

    fun start() {
        isOnline = true
        maps.forEach(collisionMap::applyCollision)
    }

    fun processLoginRequest() {
        if (!isOnline) return

        loginRequests.entries.take(150).onEach {
            players.add(it.key)
            it.key.login()
        }.also(loginRequests.entries::removeAll)
    }

    fun processLogoutRequest() {
        if (!isOnline) return

        logoutRequest.entries.onEach {
            players.remove(it.key)
            it.key.logout()
        }

        logoutRequest.clear()
    }

    fun requestLogin(session: Session, player: Player) {
        this.loginRequests[player] = session
    }

    fun requestLogout(session: Session, player: Player) {
        this.logoutRequest[player] = session
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}
