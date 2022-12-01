package com.osrs.game.actor

import com.osrs.api.location.Location

abstract class Actor {
    abstract var location: Location
    var index = 0
}
