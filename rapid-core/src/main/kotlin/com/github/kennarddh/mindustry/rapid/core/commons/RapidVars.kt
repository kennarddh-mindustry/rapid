package com.github.kennarddh.mindustry.rapid.core.commons

object RapidVars {
    val port: Int
        get() = System.getenv("PORT")?.toInt() ?: 6567
}