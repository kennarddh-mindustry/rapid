package com.github.kennarddh.mindustry.rapid.core.handlers

import com.github.kennarddh.mindustry.genesis.core.handlers.Handler
import com.github.kennarddh.mindustry.rapid.core.commons.Logger
import mindustry.net.Administration.Config

class ServerPresenceHandler : Handler {
    override suspend fun onInit() {
        Config.serverName.set("Rapid")
        Config.desc.set("Rapid")

        Logger.info("Server presence done")
    }
}