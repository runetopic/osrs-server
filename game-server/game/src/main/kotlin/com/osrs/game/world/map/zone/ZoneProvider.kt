package com.osrs.game.world.map.zone

import com.google.inject.Provider
import com.google.inject.Singleton

@Singleton
class ZoneProvider : Provider<Zones> {
    override fun get(): Zones = Zones()
}
