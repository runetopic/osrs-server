package com.osrs.game.container

import com.osrs.cache.entry.config.obj.ObjEntryProvider
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.type.server.UpdateContainerFullPacket

class Inventory(
    val player: Player,
    objEntryProvider: ObjEntryProvider
) : Container(INVENTORY_CONTAINER_KEY, INVENTORY_CAPACITY, objEntryProvider) {

    fun refresh() {
        player.session.write(
            UpdateContainerFullPacket(
                PACKED_INVENTORY_ID,
                INVENTORY_CONTAINER_KEY,
                this
            )
        )
    }

    companion object {
        const val INVENTORY_ID = 149
        const val PACKED_INVENTORY_ID = INVENTORY_ID shl 16 or -1
        const val INVENTORY_CONTAINER_KEY = 93
        const val INVENTORY_CAPACITY = 28
    }
}
