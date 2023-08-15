package com.osrs.game.ui

import com.osrs.game.actor.player.Player

/**
 * @author Tyler Telis
 */
open class UserInterfaceListener(
    private val player: Player,
    val userInterface: UserInterface
) {
    private var onOpenEvent: OpenEvent? = null
    private var onButtonClickEvent: OnButtonClickEvent? = null
    private val children = mutableMapOf<Int, OnButtonClickEvent>()
    private val actions = mutableMapOf<String, OnButtonClickEvent>()

    fun open(openEvent: OpenEvent) {
        this.onOpenEvent = openEvent
    }

    fun open(openEvent: UserInterfaceEvent.OpenEvent) {
        this.onOpenEvent?.invoke(player, openEvent)
    }

    fun click(buttonClickEvent: UserInterfaceEvent.ButtonClickEvent) {
        this.onButtonClickEvent?.invoke(player, buttonClickEvent)
        this.children[buttonClickEvent.childId]?.invoke(player, buttonClickEvent)
        this.actions[buttonClickEvent.action]?.invoke(player, buttonClickEvent)
    }

    fun click(onButtonClickEvent: OnButtonClickEvent) {
        this.onButtonClickEvent = onButtonClickEvent
    }

    fun click(childId: Int, onButtonClickEvent: OnButtonClickEvent) {
        this.children[childId] = onButtonClickEvent
    }

    fun click(action: String, onButtonClickEvent: OnButtonClickEvent) {
        this.actions[action] = onButtonClickEvent
    }
}
