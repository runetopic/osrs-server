package com.osrs.game.world.map.path

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import org.rsmod.pathfinder.PathFinder
import org.rsmod.pathfinder.ZoneFlags

@Singleton
class PathFinderProvider @Inject constructor(
    zoneFlags: ZoneFlags
) : Provider<PathFinder> {

    private val pathFinder = PathFinder(
        flags = zoneFlags.flags,
        useRouteBlockerFlags = false,
    )

    override fun get(): PathFinder = pathFinder
}
