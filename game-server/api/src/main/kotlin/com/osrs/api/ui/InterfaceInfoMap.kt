package com.osrs.api.ui

import com.google.inject.Singleton

@Singleton
class InterfaceInfoMap(
    private val interfaceInfoMap: Map<String, InterfaceInfo> = mutableMapOf()
) : Map<String, InterfaceInfo> by interfaceInfoMap {
    fun findById(id: Int): InterfaceInfo? = interfaceInfoMap.values.find { it.id == id }
}
