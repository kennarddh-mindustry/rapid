package com.github.kennarddh.mindustry.rapid.core.commons

import arc.math.Mathf
import mindustry.gen.Player

fun Player.distanceFrom(other: Player): Float = distanceFrom(other.x, other.y)

fun Player.distanceFrom(otherX: Float, otherY: Float): Float =
    Mathf.sqrt(Mathf.pow(x - otherX, 2f) + Mathf.pow(y - otherY, 2f))

