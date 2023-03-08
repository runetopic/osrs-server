package com.osrs.game.controller

import com.osrs.game.world.World

abstract class Controller<out T : Controllable> {
    var index: Int = -1
    abstract fun process(world: World)
}
