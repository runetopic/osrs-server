package com.osrs.game.network.packet.client.handler.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.entry.config.enum.EnumTypeProvider
import com.osrs.common.ui.InterfaceInfoMap
import com.osrs.common.ui.isModal
import com.osrs.game.actor.player.Player
import com.osrs.game.network.packet.client.WindowStatusPacket
import com.osrs.game.network.packet.client.handler.PacketHandler
import com.osrs.game.network.packet.server.IfOpenSubPacket
import com.osrs.game.network.packet.server.IfOpenTopPacket
import com.osrs.game.ui.InterfaceLayout
import com.osrs.game.ui.UserInterface
import com.osrs.game.ui.packInterface

@Singleton
class WindowStatusPacketHandler @Inject constructor(
    private val interfaceInfoMap: InterfaceInfoMap,
    private val enums: EnumTypeProvider
) : PacketHandler<WindowStatusPacket>() {
    private val gameInterfaces = setOf(
        UserInterface.AccountManagement,
        UserInterface.Settings,
        UserInterface.Inventory,
        UserInterface.MiniMap,
        UserInterface.ChatBox,
        UserInterface.Logout,
        UserInterface.Emotes,
        UserInterface.Magic,
        UserInterface.MusicPlayer,
        UserInterface.Skills,
        UserInterface.WornEquipment,
        UserInterface.Friends,
        UserInterface.Prayer,
        UserInterface.CombatOptions,
        UserInterface.CharacterSummary,
        UserInterface.UnknownOverlay,
        UserInterface.ChatChannel
    )

    override fun handlePacket(packet: WindowStatusPacket, player: Player) {
        val layout = enumValues<InterfaceLayout>().find { it.id == packet.displayMode } ?: return

        if (player.interfaces.layout != layout) {
            player.interfaces.layout = layout
            println("Switching game mode...TODO impl")
        } else {
            println("Setting game mode")
            sendInterfaceLayout(layout, player)
        }
    }

    private fun sendInterfaceLayout(layout: InterfaceLayout, player: Player) {
        player.write(IfOpenTopPacket(layout.interfaceId))

        gameInterfaces.forEach {
            val info = interfaceInfoMap[it.name] ?: return@forEach
            val childId = info.resizableChildId.enumChildForLayout(layout)

            player.interfaces += it

            player.write(
                IfOpenSubPacket(
                    interfaceId = info.id,
                    toInterface = layout.interfaceId.packInterface(childId),
                    clickThrough = !info.isModal()
                )
            )
        }
    }

    private fun Int.enumChildForLayout(layout: InterfaceLayout): Int =
        (enums[layout.enumId]?.params?.get(InterfaceLayout.RESIZABLE.interfaceId.packInterface(this)) as? Int)?.and(0xffff) ?: 0
}
