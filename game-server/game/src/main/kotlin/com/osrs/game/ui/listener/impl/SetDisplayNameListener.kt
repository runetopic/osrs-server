package com.osrs.game.ui.listener.impl

import com.google.inject.Singleton
import com.osrs.api.util.packInterface
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.write
import com.osrs.game.network.packet.type.server.ClientScriptPacket
import com.osrs.game.network.packet.type.server.IfOpenSubPacket
import com.osrs.game.ui.UserInterface.SetDisplayName
import com.osrs.game.ui.listener.InterfaceListener

@Singleton
class SetDisplayNameListener : InterfaceListener<SetDisplayName> {
    override fun onOpen(player: Player, userInterface: SetDisplayName) {
        player.messageBox("Setting your name", SET_DISPLAY_NAME_MESSAGE)
    }

    override fun onClick(player: Player, childId: Int, userInterface: SetDisplayName) {
        when (childId) {
            LOOK_UP_BUTTON_CHILD_ID -> player.setSearchStatus(true)
            18 -> {
                player.setSearchStatus(false)
            }
        }
    }

    private fun Player.setSearchStatus(enabled: Boolean) {
        write(
            ClientScriptPacket(
                LOOK_UP_NAME_BUTTON_STATUS,
                arrayOf(
                    INTERFACE_ID.packInterface(18),
                    INTERFACE_ID.packInterface(19),
                    INTERFACE_ID.packInterface(12),
                    INTERFACE_ID.packInterface(8),
                    INTERFACE_ID.packInterface(7),
                    if (enabled) 1 else 0
                )
            )
        )
    }

    private fun Player.messageBox(title: String, message: String) {
        write(
            IfOpenSubPacket(
                interfaceId = 263,
                toInterface = 162.packInterface(558),
                isModal = false
            )
        )

        write(ClientScriptPacket(SET_DISPLAY_NAME_SCRIPT, arrayOf("<col=0000ff>${title}</col><br>${message}")))
    }

    companion object {
        private const val INTERFACE_ID = 558
        private const val LOOK_UP_BUTTON_CHILD_ID = 7
        private const val SET_DISPLAY_NAME_SCRIPT = 1974
        private const val LOOK_UP_NAME_BUTTON_STATUS = 4139
        private const val SET_DISPLAY_NAME_MESSAGE = "Before you get started, you'll need to set a display name. Please use the open interface to set one."
    }
}
