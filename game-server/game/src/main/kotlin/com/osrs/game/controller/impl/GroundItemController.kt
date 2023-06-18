package com.osrs.game.controller.impl

import com.osrs.api.item.FloorItem
import com.osrs.game.controller.Controller
import com.osrs.game.controller.ControllerManager.removeController
import com.osrs.game.world.World
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjAddRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjRemoveRequest

class GroundItemController(
    private val floorItem: FloorItem
) : Controller<FloorItem>() {

    override fun process(world: World) {
        val zone = world.zone(floorItem.location)

        floorItem.timer--

        if (floorItem.timer == 100) {
            zone.update(ObjAddRequest(floorItem))
        } else if (floorItem.timer == 0) {
            zone.update(ObjRemoveRequest(floorItem))
            removeController(this)
        }
    }
}
