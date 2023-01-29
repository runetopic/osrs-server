package com.osrs.game.ui

class Interfaces(
    private val open: MutableList<UserInterface> = mutableListOf()
) : List<UserInterface> by open {
    var layout: InterfaceLayout = InterfaceLayout.RESIZABLE

    operator fun plusAssign(userInterface: UserInterface) {
        open += userInterface
    }

    operator fun minusAssign(userInterface: UserInterface) {
        open -= userInterface
    }
}

fun Int.packInterface(childId: Int = 0) = this shl 16 or childId
