package com.osrs.game.container

import com.osrs.cache.entry.config.obj.ObjEntryProvider
import com.osrs.game.item.Item

abstract class Container(
    val id: Int,
    val capacity: Int,
    private val objEntryProvider: ObjEntryProvider,
    private val items: MutableList<Item?> = MutableList(capacity) { null },
) : List<Item?> by items {

    protected fun add(item: Item, slotId: Int = slotId(item), function: (Item).(List<Int>) -> Unit = {}): Boolean {
        val entry = objEntryProvider[item.id] ?: return false

        when {
            entry.isStackable && slotId == -1 -> {
                val nextSlot = nextAvailableSlot()
                if (nextSlot == -1) return false
                items[nextSlot] = item
                function.invoke(item, listOf(nextSlot))
                return true
            }
            slotId != -1 -> return replaceItem(slotId, item, function)
            else -> return addItemNonStacking(item, function)
        }
    }

    protected fun remove(slotId: Int, item: Item, amount: Int = item.amount, function: ContainerUpdate): Boolean {
        if (slotId == -1) return false

        if (item.amount - amount <= 0) {
            items[slotId] = null
            function.invoke(item, listOf(slotId))
            return true
        }

        items[slotId] = item.copy(id = id, amount = item.amount - amount)
        function.invoke(item, listOf(slotId))
        return true
    }

    /**
     * Add an item to the container and invokes the function.
     * This function is only ever used by items that are not stackable.
     * @param item The item to be added to the container.
     * @param function This function is invoked with the item and slots that were updated.
     */
    private fun addItemNonStacking(
        item: Item,
        function: ContainerUpdate
    ): Boolean {
        val entry = objEntryProvider[item.id] ?: return false

        if (entry.isStackable) return false

        val slotsChanged = mutableListOf<Int>()

        repeat(item.amount) {
            val nextSlot = nextAvailableSlot()

            if (nextSlot == -1) {
                function.invoke(item, slotsChanged)
                return false
            }

            slotsChanged.add(nextSlot)
            items[nextSlot] = item.copy(id = item.id, amount = 1)
        }

        function.invoke(item, slotsChanged)
        return true
    }

    /**
     * Replaces every item with null to clear out the container.
     */
    protected fun clear() {
        items.replaceAll { null }
    }

    /**
     * Clears the container and invokes a function
     */
    protected fun clear(function: () -> Unit) {
        clear()
        function.invoke()
    }

    fun availableSlots(): Int = items.count { it == null }

    /**
     * This returns if the container is full or not.
     */
    fun isFull(): Boolean = availableSlots() == 0

    /**
     * Checks if the container is not full, and we have an available slot to fill.
     */
    fun hasSpace(): Boolean = !isFull() && nextAvailableSlot() != -1

    /**
     * Get the next available slot.
     * This will find the first index of the first null item it encounters.
     */
    fun nextAvailableSlot(): Int = items.indexOfFirst { it == null }

    /**
     * Replaces an item at a slotId
     * If the existing item is found, and it's stackable we just increment the existing item's amount.
     * @param slotId The slot id that we are replacing the item at.
     * @param item The item we're replacing at the slot.
     * @param function This function is invoked with the item and slots that were updated.
     */
    private fun replaceItem(
        slotId: Int,
        item: Item,
        function: ContainerUpdate
    ): Boolean {
        val entry = objEntryProvider[item.id] ?: return false

        val existingItem = items[slotId] ?: return false

        if ((item.amount + existingItem.amount) < 0) return false

        return when {
            entry.isStackable && !isFull() -> {
                items[slotId] = item.copy(
                    id = item.id,
                    amount = item.amount + existingItem.amount
                )
                function.invoke(item, listOf(slotId))
                true
            }
            else -> addItemNonStacking(item, function)
        }
    }

    /**
     * Sets a specific item at a desired slot id.
     * @param slotId The slot id we're placing this item into.
     * @param item The item to set
     * @param function This function is invoked with the item and slots that were updated.
     */
    fun setItem(slotId: Int, item: Item, function: (Item).(Int) -> Unit) {
        items[slotId] = item
        function.invoke(item, slotId)
    }

    /**
     * Gets the slotId for the provided item id.
     * @param id The item id to look up.
     */
    fun slotId(id: Int) = items.indexOfFirst { it?.id == id }

    /**
     * Gets the slotId for the provided item.
     * @param item The item to lookup
     */
    fun slotId(item: Item) = items.indexOfFirst { it?.id == item.id }

    /**
     * Finds the first item by the slot id.
     * @param slot The slot id to search for.
     * @return The item found within the container.
     */
    fun firstBySlot(slot: Int): Item? = items[slot]

    /**
     * Hard replace this entire container with another list of items.
     * This is only necessary for the player json serialization.
     * @param src The src list of items to replace this container with.
     */
    fun replaceAll(src: List<Item?>) {
        src.forEachIndexed(items::set)
    }

    fun hasItem(id: Int): Boolean = slotId(id) != -1
}


typealias ContainerUpdate = (Item).(List<Int>) -> Unit
