package com.osrs.game.world.map.path

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import org.rsmod.pathfinder.StepValidator
import org.rsmod.pathfinder.ZoneFlags

@Singleton
class StepValidatorProvider @Inject constructor(
    zoneFlags: ZoneFlags
) : Provider<StepValidator> {

    private val stepValidator = StepValidator(zoneFlags.flags)

    override fun get(): StepValidator = stepValidator
}
