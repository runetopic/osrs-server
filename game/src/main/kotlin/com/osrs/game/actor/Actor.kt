package com.osrs.game.actor

import com.osrs.game.world.map.Location

abstract class Actor {
    abstract var location: Location
    var index = 0
}
