package com.osrs.game.actor

import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Player
import kotlinx.serialization.Serializable

typealias PlayerList = ActorList<Player?>
typealias NPCList = ActorList<NPC?>

/**
 * @author Tyler Telis
 */
@Serializable
class ActorList<T>(
    private val initialCapacity: Int,
    private val actors: MutableList<T?> = createMutableList<T?>(initialCapacity)
) : List<T?> by actors {
    override val size: Int get() = actors.count { it != null }
    val indices: IntRange get() = actors.indices
    val capacity: Int get() = actors.size

    @Suppress("UNCHECKED_CAST")
    fun add(actor: Actor): Boolean {
        val index = actors.freeIndex()
        if (index == INVALID_INDEX) return false
        actors[index] = actor as T
        actor.index = index
        return actors[actor.index] != null
    }

    fun remove(actor: Actor): Boolean = when {
        actor.index == INVALID_INDEX -> false
        actors[actor.index] != actor -> false
        else -> {
            actors[actor.index] = null
            actors[actor.index] == null
        }
    }

    override fun isEmpty(): Boolean = size == 0

    private fun <T> List<T>.freeIndex(): Int = (INDEX_PADDING until indices.last).firstOrNull { this[it] == null } ?: INVALID_INDEX

    private companion object {
        const val INVALID_INDEX = -1
        const val INDEX_PADDING = 1

        @Suppress("UNCHECKED_CAST")
        fun <T> createMutableList(size: Int): MutableList<T?> = (arrayOfNulls<Any?>(size) as Array<T?>).toMutableList()
    }
}
