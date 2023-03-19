package com.osrs.game.network.packet.builder.impl.render

import com.google.inject.Singleton
import com.osrs.game.actor.npc.NPC
import com.osrs.game.world.World

/**
 * @author Jordan Abraham
 */
@Singleton
class NPCUpdateBlocks(
    val highDefinitionUpdates: Array<ByteArray?> = arrayOfNulls(World.MAX_NPCS)
) {
    fun buildPendingUpdateBlocks(npc: NPC) {
        if (npc.renderer.hasHighDefinitionUpdate()) {
        }
    }

    fun clear() {
        highDefinitionUpdates.fill(null)
    }
}
