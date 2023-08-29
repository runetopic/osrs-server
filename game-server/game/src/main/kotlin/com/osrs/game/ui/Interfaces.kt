package com.osrs.game.ui

import com.osrs.api.ui.InterfaceInfo
import com.osrs.api.ui.InterfaceInfoMap
import com.osrs.api.util.packInterface
//import com.osrs.cache.entry.config.enum.EnumEntryProvider
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.write
import com.osrs.game.network.packet.type.server.IfOpenSubPacket
import com.osrs.game.network.packet.type.server.IfOpenTopPacket
import com.osrs.game.ui.InterfaceLayout.RESIZABLE

class Interfaces(
    val player: Player,
    private val interfaceInfoMap: InterfaceInfoMap,
//    private val enumEntryProvider: EnumEntryProvider,
    private val interfaceListener: InterfaceListener,
    private val open: MutableList<UserInterface> = mutableListOf()
) : MutableList<UserInterface> by open {
    var layout: InterfaceLayout = RESIZABLE

    val listeners = mutableMapOf<Int, UserInterfaceListener>()

    fun sendInterfaceLayout(layout: InterfaceLayout) = player.write(IfOpenTopPacket(layout.interfaceId))

    operator fun plusAssign(userInterface: UserInterface) {
        add(userInterface)

        val info = interfaceInfoMap[userInterface.name] ?: return

        val childId = info.resizableChildId.enumChildForLayout(layout)

        val listener = interfaceListener.createListener(userInterface, player)

        listeners[info.id] = listener

        player.write(
            IfOpenSubPacket(
                interfaceId = info.id,
                toInterface = layout.interfaceId.packInterface(childId),
                isModal = info.isModal()
            )
        )

        listener.open(
            UserInterfaceEvent.OpenEvent(
                interfaceId = info.id
            )
        )
    }

    private fun InterfaceInfo.isModal() = resizableChildId == MODAL_CHILD_ID || resizableChildId == MODAL_CHILD_ID_EXTENDED

    private fun Int.enumChildForLayout(layout: InterfaceLayout): Int = 0
     /*   (enumEntryProvider[layout.enumId]?.params?.get(RESIZABLE.interfaceId.packInterface(this)) as? Int)?.and(0xffff) ?: 0*/

    companion object {
        const val MODAL_CHILD_ID = 16
        const val MESSAGE_BOX_CHILD = 558
        const val MODAL_CHILD_ID_EXTENDED = 17
        const val INVENTORY_CHILD_ID = 73
    }
}
