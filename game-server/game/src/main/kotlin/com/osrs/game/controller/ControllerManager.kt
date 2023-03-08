package com.osrs.game.controller

object ControllerManager {

    val controllers = arrayOfNulls<Controller<*>>(Short.MAX_VALUE.toInt())

    fun addController(controller: Controller<*>) {
        val nextIndex = controllers.indexOf(null)
        controller.index = nextIndex
        this.controllers[nextIndex] = controller
    }

    fun removeController(controller: Controller<*>) {
        if (controller.index == -1) return
        this.controllers[controller.index] = null
    }
}
