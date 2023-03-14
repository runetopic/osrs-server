package com.osrs.game.projectile

data class Projectile(
    val id: Int,
    val startHeight: Int,
    val endHeight: Int,
    val delay: Int,
    val angle: Int,
    val lengthAdjustment: Int,
    val distOffset: Int,
    val stepMultiplier: Int
) {
    fun flightTime(distance: Int) = delay + lengthAdjustment + (distance * stepMultiplier)
}
