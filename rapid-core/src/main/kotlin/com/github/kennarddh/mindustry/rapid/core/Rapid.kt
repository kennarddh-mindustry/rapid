package com.github.kennarddh.mindustry.rapid.core

import com.github.kennarddh.mindustry.genesis.core.Genesis
import com.github.kennarddh.mindustry.genesis.core.commons.AbstractPlugin
import com.github.kennarddh.mindustry.rapid.core.commons.extensions.Logger
import com.github.kennarddh.mindustry.rapid.core.handlers.RapidHandler
import com.github.kennarddh.mindustry.rapid.core.handlers.ServerPresenceHandler
import com.github.kennarddh.mindustry.rapid.core.handlers.StartHandler
import com.github.kennarddh.mindustry.rapid.core.handlers.WelcomeHandler
import com.github.kennarddh.mindustry.rapid.core.handlers.vote.kick.VoteKickCommandHandler
import com.github.kennarddh.mindustry.rapid.core.handlers.vote.rtv.RTVCommandHandler
import com.github.kennarddh.mindustry.rapid.core.handlers.vote.skip_wave.SkipWaveCommandHandler

@Suppress("unused")
class Rapid : AbstractPlugin() {
    override suspend fun onInit() {
        Logger.info("Registering handlers")

        Genesis.registerHandler(RapidHandler())

        Genesis.registerHandler(StartHandler())

        Genesis.registerHandler(VoteKickCommandHandler())
        Genesis.registerHandler(RTVCommandHandler())
        Genesis.registerHandler(SkipWaveCommandHandler())

        Genesis.registerHandler(ServerPresenceHandler())
        Genesis.registerHandler(WelcomeHandler())

        Logger.info("Loaded")
    }
}