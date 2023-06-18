package com.osrs.game.actor.render.type

import com.osrs.api.map.location.Location
import com.osrs.game.actor.render.RenderType

data class ExactMove(
    val currentLocation: Location,
    val firstLocation: Location,
    val secondLocation: Location = Location.None,
    val firstDuration: Int = 0,
    val secondDuration: Int = 0,
    val angle: Int = 0
) : RenderType
