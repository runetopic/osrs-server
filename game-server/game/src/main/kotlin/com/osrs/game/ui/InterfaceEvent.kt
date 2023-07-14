package com.osrs.game.ui

/**
 * @author Tyler Telis
 */
sealed class InterfaceEvent(val value: Int) {
    object Continue : InterfaceEvent(1 shl 0)
    object ClickOption1 : InterfaceEvent(1 shl 1)
    object ClickOption2 : InterfaceEvent(1 shl 2)
    object ClickOption3 : InterfaceEvent(1 shl 3)
    object ClickOption4 : InterfaceEvent(1 shl 4)
    object ClickOption5 : InterfaceEvent(1 shl 5)
    object ClickOption6 : InterfaceEvent(1 shl 6)
    object ClickOption7 : InterfaceEvent(1 shl 7)
    object ClickOption8 : InterfaceEvent(1 shl 8)
    object ClickOption9 : InterfaceEvent(1 shl 9)
    object ClickOption10 : InterfaceEvent(1 shl 10)
    object UseOnGroundItem : InterfaceEvent(1 shl 11)
    object UseOnNPC : InterfaceEvent(1 shl 12)
    object UseOnObject : InterfaceEvent(1 shl 13)
    object UseOnPlayer : InterfaceEvent(1 shl 14)
    object UseOnInventory : InterfaceEvent(1 shl 15)
    object UseOnComponent : InterfaceEvent(1 shl 16)
    object DragDepth1 : InterfaceEvent(1 shl 17)
    object DragDepth2 : InterfaceEvent(2 shl 17)
    object DragDepth3 : InterfaceEvent(3 shl 17)
    object DragDepth4 : InterfaceEvent(4 shl 17)
    object DragDepth5 : InterfaceEvent(5 shl 17)
    object DragDepth6 : InterfaceEvent(6 shl 17)
    object DragDepth7 : InterfaceEvent(7 shl 17)
    object DragTargetable : InterfaceEvent(1 shl 20)
    object ComponentTargetable : InterfaceEvent(1 shl 21)
}
