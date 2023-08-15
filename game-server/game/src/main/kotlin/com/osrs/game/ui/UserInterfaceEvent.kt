package com.osrs.game.ui

import com.osrs.game.actor.player.Player
import com.osrs.game.ui.UserInterfaceEvent.ButtonClickEvent
import com.osrs.game.ui.UserInterfaceEvent.CloseEvent
import com.osrs.game.ui.UserInterfaceEvent.CreateEvent
import com.osrs.game.ui.UserInterfaceEvent.OpHeldEvent
import com.osrs.game.ui.UserInterfaceEvent.OpenEvent

sealed class UserInterfaceEvent {
    data class CreateEvent(
        val interfaceId: Int
    ) : UserInterfaceEvent()

    data class OpenEvent(
        val interfaceId: Int
    ) : UserInterfaceEvent()

    data class CloseEvent(
        val interfaceId: Int
    ) : UserInterfaceEvent()

    data class ButtonClickEvent(
        val index: Int,
        val interfaceId: Int,
        val childId: Int,
        val slotId: Int,
        val itemId: Int,
        val action: String
    ) : UserInterfaceEvent()

    data class IfEvent(
        val slots: IntRange,
        val event: Int,
    ) : UserInterfaceEvent()

    data class OpHeldEvent(
        val index: Int,
        val fromInterfaceId: Int,
        val fromChildId: Int,
        val fromSlotId: Int,
        val fromItemId: Int,
        val toInterfaceId: Int,
        val toChildId: Int,
        val toSlotId: Int,
        val toItemId: Int
    ) : UserInterfaceEvent()
}

typealias OnButtonClickEvent = (Player).(ButtonClickEvent) -> Unit
typealias OpenEvent = (Player).(OpenEvent) -> Unit
typealias OnCreateEvent = (Player).(CreateEvent) -> Unit
typealias OnCloseEvent = (Player).(CloseEvent) -> Unit
typealias OnOpHeldEvent = (Player).(OpHeldEvent) -> Unit
