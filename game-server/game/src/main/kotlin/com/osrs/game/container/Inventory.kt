package com.osrs.game.container

import com.osrs.cache.entry.config.obj.ObjEntryProvider
import com.osrs.game.actor.player.Player
import com.osrs.game.item.Item
import com.osrs.game.network.packet.type.server.UpdateContainerFullPacket

const val INVENTORY_ID = 149
const val PACKED_INVENTORY_ID = INVENTORY_ID shl 16 or -1
const val INVENTORY_CONTAINER_KEY = 93
const val INVENTORY_CAPACITY = 28

class Inventory(
    val player: Player,
    objEntryProvider: ObjEntryProvider
) : Container(INVENTORY_CONTAINER_KEY, INVENTORY_CAPACITY, objEntryProvider) {

    fun sendInventory() {
        add(Item(995, Int.MAX_VALUE))
        add(Item(995, Int.MAX_VALUE))
        player.session.write(
            UpdateContainerFullPacket(
                PACKED_INVENTORY_ID,
                INVENTORY_CONTAINER_KEY,
                this
            )
        )
    }
}
