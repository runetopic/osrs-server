package com.osrs.game.world

import com.google.inject.Provider
import com.google.inject.Singleton

@Singleton
class WorldProvider : Provider<World> {
    // TODO add support for multiple worlds but for now this will work
    override fun get(): World = World(1)
}
