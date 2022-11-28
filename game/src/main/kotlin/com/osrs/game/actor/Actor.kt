package com.osrs.game.actor

import com.osrs.game.world.map.Location

abstract class Actor {
    open var location: Location = Location(322, 3222, 0)
    var index = 0
}
