package com.github.kennarddh.mindustry.rapid.core.handlers.vote.rtv

import arc.Events
import com.github.kennarddh.mindustry.genesis.core.commands.annotations.ClientSide
import com.github.kennarddh.mindustry.genesis.core.commands.annotations.Command
import com.github.kennarddh.mindustry.genesis.core.commons.runOnMindustryThread
import com.github.kennarddh.mindustry.rapid.core.handlers.vote.AbstractVoteCommand
import com.github.kennarddh.mindustry.rapid.core.handlers.vote.VoteSession
import mindustry.game.EventType.GameOverEvent
import mindustry.game.Team
import mindustry.gen.Call
import mindustry.gen.Groups
import mindustry.gen.Player
import kotlin.math.ceil
import kotlin.time.Duration.Companion.minutes

class RTVCommandHandler : AbstractVoteCommand<Byte>("rtv", 2.minutes) {
    @Command(["rtv", "change-map"])
    @ClientSide
    suspend fun rtv(player: Player, vote: Boolean = true) {
        if (!getIsVoting()) {
            if (vote) {
                start(player, 1)
            } else {
                player.sendMessage("[#ff0000]Cannot vote no for '$name' because there is no '$name' vote session in progress.")
            }

            return
        }

        vote(player, true)
    }

    @Command(["rtv-cancel"])
    @ClientSide
    suspend fun cancelCommand(player: Player) {
        cancel(player)
    }

    override fun getRequiredVotes(): Int = ceil(Groups.player.size() * 3f / 4f).toInt()

    override suspend fun onSuccess(session: VoteSession<Byte>) {
        Call.sendMessage("[#00ff00]RTV success. Changing map.")

        runOnMindustryThread {
            Events.fire(GameOverEvent(Team.crux))
        }
    }

    override suspend fun getSessionDetails(session: VoteSession<Byte>): String {
        return "Type [accent]/rtv y[] or [accent]/rtv n[] to vote."
    }
}