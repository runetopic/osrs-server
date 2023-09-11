package com.osrs.api.map

import com.google.inject.Singleton
import com.osrs.api.resource.NPCConfig

@Singleton
class NPCConfigList(
    private val spawns: List<NPCConfig> = mutableListOf()
) : List<NPCConfig> by spawns
