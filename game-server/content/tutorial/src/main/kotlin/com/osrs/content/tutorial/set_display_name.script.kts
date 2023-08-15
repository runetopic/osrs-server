package com.osrs.content.tutorial

import com.github.michaelbull.logging.InlineLogger
import com.osrs.api.util.packInterface
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.write
import com.osrs.game.network.packet.type.server.ClientScriptPacket
import com.osrs.game.network.packet.type.server.IfOpenSubPacket
import com.osrs.game.ui.UserInterface.SetDisplayName
import com.osrs.script.content.userInterface

private val logger = InlineLogger()

private val interfaceId = 558
private val inputChildId = 7
private val setDisplayNameScriptId = 1974
private val lookupButtonScriptId = 4139

userInterface<SetDisplayName> {
    open {
        toggleSearchBox(true)
        messageBox(
            title = "Setting your name",
            message = "Before you get started, you'll need to set a display name. Please use the open interface to set one."
        )
    }

    click(childId = inputChildId) {
        logger.info { "Clicked lookup" }
        toggleSearchBox(true)
    }
}

fun Player.toggleSearchBox(enabled: Boolean) {
    write(
        ClientScriptPacket(
            lookupButtonScriptId,
            arrayOf(
                interfaceId.packInterface(18),
                interfaceId.packInterface(19),
                interfaceId.packInterface(12),
                interfaceId.packInterface(8),
                interfaceId.packInterface(7),
                if (enabled) 1 else 0
            )
        )
    )
}

// TODO extract this out into a helper function inside of player ext and also create named scripts
fun Player.messageBox(title: String, message: String) {
    write(
        IfOpenSubPacket(
            interfaceId = 263,
            toInterface = 162.packInterface(558),
            isModal = false
        )
    )

    write(ClientScriptPacket(setDisplayNameScriptId, arrayOf("<col=0000ff>${title}</col><br>${message}")))
}
