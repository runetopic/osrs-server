package com.osrs.game.controller.impl

import com.osrs.game.controller.Controller
import com.osrs.game.controller.ControllerManager.removeController
import com.osrs.game.item.FloorItem
import com.osrs.game.world.World
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjAddRequest
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjRemoveRequest

class GroundItemController(
    private val floorItem: FloorItem,
) : Controller<FloorItem>() {

    override fun process(world: World) {
        val zone = world.zone(floorItem.location)

        println("processing ground item controller")

        floorItem.timer--

        if (floorItem.timer == 20) {
            zone.update(ObjAddRequest(floorItem))
        } else if (floorItem.timer == 10) {
            zone.update(ObjRemoveRequest(floorItem))
            removeController(this)
        }
    }
}
