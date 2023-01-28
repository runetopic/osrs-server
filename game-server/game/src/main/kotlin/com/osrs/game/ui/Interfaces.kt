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
