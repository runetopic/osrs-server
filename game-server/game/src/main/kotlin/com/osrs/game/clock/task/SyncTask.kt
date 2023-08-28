package com.osrs.game.clock.task

interface SyncTask {
    fun sync(tick: Int)
}
