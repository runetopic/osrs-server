package com.osrs.api.map

import com.google.inject.Singleton

@Singleton
class MapSquares(
    private val squares: Map<Int, MapSquare> = mutableMapOf()
) : Map<Int, MapSquare> by squares
