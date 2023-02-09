package com.osrs.game.container

import com.osrs.cache.entry.config.obj.ObjEntryProvider
import com.osrs.game.item.Item

abstract class Container(
    val id: Int,
    val capacity: Int,
    private val objEntryProvider: ObjEntryProvider,
    private val items: MutableList<Item?> = MutableList(capacity) { null },
) : List<Item?> by items {

    fun availableSlots(): Int = items.count { it == null }

    protected fun add(item: Item, slotId: Int = slotId(item), function: (Item).(List<Int>) -> Unit = {}): Boolean {
        val slotId = slotId
        val entry = objEntryProvider[item.id] ?: return false

        return true
    }

    fun nextAvailableSlot(): Int = items.indexOfFirst { it == null }

    fun slotId(id: Int) = items.indexOfFirst { it?.id == id }

    fun slotId(item: Item) = items.indexOfFirst { it?.id == item.id }
}

typealias ContainerUpdate = (Item).(List<Int>) -> Unit
