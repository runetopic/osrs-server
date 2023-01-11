package com.osrs.game.tick.task.world

import com.osrs.game.tick.task.SyncTask
import com.osrs.game.world.World

class WorldSyncTask(val world: World) : SyncTask {
    override fun sync() {
        world.processLoginRequest()
    }
}
