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
        // TODO: extract this into a re-usable function for dialogue and cleanup the scripts and setup the button status.
        player.write(
            IfOpenSubPacket(
                interfaceId = 263,
                toInterface = 162.packInterface(558),
                isModal = false
            )
        )
        // ClientScript(id = 4144, converted = [558:18, 558:19, 558:12, 558:8, 558:7, "Darkshade"], raw = [36569106, 36569107, 36569100, 36569096, 36569095, "Darkshade"], types = [IIIIIs])

        arrayOf(
            ClientScriptPacket(SET_DISPLAY_NAME_SCRIPT, arrayOf(SET_DISPLAY_NAME_MESSAGE)),
            ClientScriptPacket(2524, arrayOf(-1, -1)),
            ClientScriptPacket(id = LOOK_UP_NAME_BUTTON_STATUS, arrayOf(36569106, 36569107, 36569100, 36569096, 36569095, 1))
//            ClientScriptPacket(id = 4144, arrayOf(36569106, 36569107, 36569100, 36569096, 36569095, "Darkshade"))
        ).forEach(player::write)
    }

    companion object {
        private const val SET_DISPLAY_NAME_SCRIPT = 1974
        private const val LOOK_UP_NAME_BUTTON_STATUS = 4139
        private const val SET_DISPLAY_NAME_MESSAGE = "<col=0000ff>Setting your name</col><br>Before you get started, you'll need to set a display name. Please use the open interface to set one."
    }
}
