package com.github.kennarddh.mindustry.rapid.core.handlers

import arc.Core
import arc.util.Reflect
import arc.util.Timer
import com.github.kennarddh.mindustry.genesis.core.commons.runOnMindustryThread
import com.github.kennarddh.mindustry.genesis.core.events.annotations.EventHandler
import com.github.kennarddh.mindustry.genesis.core.handlers.Handler
import com.github.kennarddh.mindustry.rapid.core.commons.RapidVars
import com.github.kennarddh.mindustry.rapid.core.commons.extensions.Logger
import kotlinx.coroutines.delay
import mindustry.Vars
import mindustry.game.EventType
import mindustry.game.Gamemode
import mindustry.net.Administration.Config
import mindustry.server.ServerControl
import kotlin.time.Duration.Companion.seconds

class StartHandler : Handler {
    @EventHandler
    suspend fun onLoad(event: EventType.ServerLoadEvent) {
        Logger.info("Server load... Will host in 1 second.")

        Config.port.set(RapidVars.port)

        Logger.info("Port set to ${RapidVars.port}")

        delay(1.seconds)

        Logger.info("Hosting")

        host()
    }

    private fun host() {
        runOnMindustryThread {
            val gameMode = Gamemode.survival

            // TODO: When v147 released replace this with ServerControl.instance.cancelPlayTask()
            Reflect.get<Timer.Task>(ServerControl.instance, "lastTask")?.cancel()

            val map = Vars.maps.shuffleMode.next(gameMode, Vars.state.map)

            Vars.logic.reset()

            ServerControl.instance.lastMode = gameMode

            Core.settings.put("lastServerMode", ServerControl.instance.lastMode.name)
            Vars.world.loadMap(map, map.applyRules(ServerControl.instance.lastMode))
            Vars.state.rules = map.applyRules(gameMode)
            Vars.logic.play()

            Vars.netServer.openServer()

            Logger.info("Hosted")
        }
    }
}