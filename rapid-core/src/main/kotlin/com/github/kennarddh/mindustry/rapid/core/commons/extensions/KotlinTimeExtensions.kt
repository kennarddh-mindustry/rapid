package com.github.kennarddh.mindustry.rapid.core.commons.extensions

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Duration.toDisplayString(): String = inWholeSeconds.seconds.toString()