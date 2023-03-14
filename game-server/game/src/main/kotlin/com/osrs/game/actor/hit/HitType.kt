package com.osrs.game.actor.hit

@JvmInline
value class HitType(val id: Int) {
    companion object {
        val Normal = HitType(16)
    }
}
