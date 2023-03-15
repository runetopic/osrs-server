package com.osrs.game.world.map.zone

import com.osrs.common.item.FloorItem
import com.osrs.common.map.location.ZoneLocation
import com.osrs.common.map.location.distanceTo
import com.osrs.game.actor.Actor
import com.osrs.game.actor.player.Player
import com.osrs.game.controller.ControllerManager.addController
import com.osrs.game.controller.impl.GroundItemController
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.type.server.LocAddPacket
import com.osrs.game.network.packet.type.server.MapProjAnimPacket
import com.osrs.game.network.packet.type.server.ObjAddPacket
import com.osrs.game.network.packet.type.server.ObjRemovePacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialEnclosedPacket
import com.osrs.game.network.packet.type.server.UpdateZonePartialFollowsPacket
import com.osrs.game.world.map.GameObject
import com.osrs.game.world.map.zone.ZoneUpdateRequest.LocAddRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjAddRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjRemoveRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ProjectileRequest

class Zone(
    val location: ZoneLocation
) {
    private val players = HashSet<Player>()

    private val spawnedObjs = ArrayList<FloorItem>()
    private val removedObjs = ArrayList<FloorItem>()

    private val spawnedLocs = ArrayList<GameObject>()
    private val staticLocs = ArrayList<GameObject>()

    private val zoneUpdateRequest = ArrayList<ZoneUpdateRequest>()
    private val zoneUpdates = ArrayList<Packet>()

    fun enterZone(actor: Actor) {
        if (actor is Player) {
            players.add(actor)
        }
    }

    fun leaveZone(actor: Actor) {
        if (actor is Player) {
            players -= actor
        }
    }

    fun writeInitialZoneUpdates(player: Player) {
        player.sendObjs()
        player.sendLocs()
    }

    private fun Player.sendLocs() {
        for (loc in spawnedLocs) sendLocAddPacket(loc)
    }

    private fun Player.sendObjs() {
        for (obj in this@Zone.spawnedObjs) sendObjAddPacket(obj)

        for (obj in objs.filter { it.location.zoneId == this@Zone.location.id }) {
            sendObjAddPacket(obj)
        }
    }

    fun writeZoneUpdates(player: Player) {
        val baseZoneX = player.baseZoneLocation.x
        val baseZoneZ = player.baseZoneLocation.z

        val xInScene = (location.x - baseZoneX) shl 3
        val zInScene = (location.z - baseZoneZ) shl 3

        player.writeUpdates(xInScene, zInScene)
        player.writeGlobalUpdates(xInScene, zInScene)
    }

    private fun Player.writeUpdates(xInScene: Int, zInScene: Int) {
        write(UpdateZonePartialFollowsPacket(xInScene, zInScene))

        for (request in zoneUpdateRequest) {
            if (request is ObjRemoveRequest && request.receiver == -1) {
                sendObjRemovePacket(request.floorItem)
            }

            if (request is ObjAddRequest && !objs.contains(request.floorItem)) {
                sendObjAddPacket(request.floorItem)
            }
        }
    }

    private fun Player.writeGlobalUpdates(xInScene: Int, zInScene: Int) {
        ZoneManager.getGlobalZoneUpdates(this@Zone.location.packedLocation).let { updates ->
            session.write(
                UpdateZonePartialEnclosedPacket(
                    xInScene,
                    zInScene,
                    updates,
                    session.builders
                )
            )
        }
    }

    fun update(player: Player, request: ZoneUpdateRequest) {
        val baseZoneX = player.baseZoneLocation.x
        val baseZoneZ = player.baseZoneLocation.z
        val xInScene = (location.x - baseZoneX) shl 3
        val zInScene = (location.z - baseZoneZ) shl 3

        player.session.write(UpdateZonePartialFollowsPacket(xInScene, zInScene))

        if (request is ObjRemoveRequest) {
            player.objs.remove(request.floorItem)
            player.sendObjRemovePacket(request.floorItem)
        }

        if (request is ObjAddRequest) {
            player.objs.add(request.floorItem)
            player.sendObjAddPacket(request.floorItem)

            if (request.floorItem.timer != -1) {
                addController(GroundItemController(request.floorItem))
            }
        }
    }

    fun update(request: ZoneUpdateRequest) {
        zoneUpdateRequest += request

        if (request is ObjAddRequest) addObj(request)
        if (request is ObjRemoveRequest) removeObj(request)
        if (request is LocAddRequest) addLoc(request)
    }

    private fun addObj(request: ObjAddRequest) {
        spawnedObjs.add(request.floorItem)
    }

    private fun removeObj(request: ObjRemoveRequest) {
        spawnedObjs.remove(request.floorItem)
        removedObjs.add(request.floorItem)
    }

    private fun addLoc(request: LocAddRequest) {
        spawnedLocs.add(request.gameObject)
    }

    fun buildSharedUpdates(sharedUpdates: Sequence<ZoneUpdateRequest>): Sequence<Packet> = sequence {
        for (request in sharedUpdates) {
            if (request is ObjRemoveRequest) {
                yieldObjRemovePacket(request)
            }
            if (request is ProjectileRequest) {
                yieldMapAnimProjPacket(request)
            }
            if (request is LocAddRequest) {
                yieldLocAddPacket(request)
            }
        }
    }

    private fun Player.sendLocAddPacket(gameObject: GameObject) {
        val packedOffset = gameObject.location.packedOffset
        write(LocAddPacket(gameObject.id, gameObject.shape, gameObject.rotation, packedOffset))
    }

    private fun Player.sendObjAddPacket(floorItem: FloorItem) {
        val id = floorItem.id
        val quantity = floorItem.quantity
        val packedOffset = floorItem.location.packedOffset
        write(ObjAddPacket(id, quantity, packedOffset))
    }

    private fun Player.sendObjRemovePacket(floorItem: FloorItem) {
        val id = floorItem.id
        val quantity = floorItem.quantity
        val packedOffset = floorItem.location.packedOffset
        write(ObjRemovePacket(id, quantity, packedOffset))
    }

    private suspend fun SequenceScope<Packet>.yieldLocAddPacket(request: LocAddRequest) {
        val gameObject = request.gameObject
        val packedOffset = gameObject.location.packedOffset
        yield(LocAddPacket(gameObject.id, gameObject.shape, gameObject.rotation, packedOffset))
    }

    private suspend fun SequenceScope<Packet>.yieldObjRemovePacket(request: ObjRemoveRequest) {
        val floorItem = request.floorItem
        val packedOffset = floorItem.location.packedOffset
        yield(ObjRemovePacket(floorItem.id, floorItem.quantity, packedOffset))
    }

    private suspend fun SequenceScope<Packet>.yieldMapAnimProjPacket(request: ProjectileRequest) {
        val targetIndex = (request.target?.index ?: 0) + 1

        val from = request.from

        val to = request.target?.location ?: request.to

        val distanceX = to.x - from.x
        val distanceZ = to.z - from.z
        val flightTime = request.projectile.flightTime(from.distanceTo(to))

        yield(
            MapProjAnimPacket(
                id = request.projectile.id,
                startHeight = request.projectile.startHeight,
                endHeight = request.projectile.endHeight,
                delay = request.projectile.delay,
                angle = request.projectile.angle,
                distOffset = request.projectile.distOffset, // Moves the projectile closer to the end location by units of 1/128 with 128 being a tile
                packedOffset = request.from.packedOffset,
                targetIndex = targetIndex,
                distanceX = distanceX,
                distanceZ = distanceZ,
                flightTime = flightTime
            )
        )
    }

    fun clear() {
        zoneUpdates.clear()
        zoneUpdateRequest.clear()
    }

    fun getZoneUpdateRequests(): List<ZoneUpdateRequest> = zoneUpdateRequest

    fun requiresSync(): Boolean = zoneUpdateRequest.isNotEmpty()

    fun addStaticLoc(gameObject: GameObject) {
        staticLocs.add(gameObject)
    }
}
