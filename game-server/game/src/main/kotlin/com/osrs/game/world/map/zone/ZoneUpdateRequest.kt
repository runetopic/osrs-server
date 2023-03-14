package com.osrs.game.world.map.zone

import com.osrs.common.item.FloorItem
import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.projectile.Projectile
import com.osrs.game.world.map.GameObject

sealed class ZoneUpdateRequest {
    class ProjectileRequest(
        val target: Actor? = null,
        val from: Location,
        val to: Location = Location.None,
        val projectile: Projectile
    ) : ZoneUpdateRequest()

    class ObjAddRequest(
        val floorItem: FloorItem
    ) : ZoneUpdateRequest()

    class ObjUpdateRequest(
        val floorItem: FloorItem
    ) : ZoneUpdateRequest()

    class ObjRemoveRequest(
        val floorItem: FloorItem,
        val receiver: Int = -1
    ) : ZoneUpdateRequest()

    class LocAddRequest(
        val gameObject: GameObject
    ) : ZoneUpdateRequest()
}
