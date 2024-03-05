package com.github.kennarddh.mindustry.rapid.core.handlers.vote.kick

import com.github.kennarddh.mindustry.genesis.core.Genesis
import com.github.kennarddh.mindustry.genesis.core.commands.annotations.ClientSide
import com.github.kennarddh.mindustry.genesis.core.commands.annotations.Command
import com.github.kennarddh.mindustry.genesis.standard.extensions.kickWithoutLogging
import com.github.kennarddh.mindustry.rapid.core.commons.toDisplayString
import com.github.kennarddh.mindustry.rapid.core.handlers.vote.AbstractVoteCommand
import com.github.kennarddh.mindustry.rapid.core.handlers.vote.VoteSession
import mindustry.gen.Call
import mindustry.gen.Groups
import mindustry.gen.Player
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class VoteKickCommandHandler : AbstractVoteCommand<VoteKickVoteObjective>("vote kick", 1.minutes) {
    override suspend fun onInit() {
        Genesis.commandRegistry.removeCommand("votekick")
        Genesis.commandRegistry.removeCommand("vote")
    }

    @Command(["votekick", "vote-kick"])
    @ClientSide
    suspend fun startVoteKick(player: Player, target: Player, reason: String) {
        start(player, VoteKickVoteObjective(target, reason))
    }

    @Command(["vote"])
    @ClientSide
    suspend fun voteCommand(player: Player, vote: Boolean) {
        vote(player, vote)
    }

    @Command(["vote-cancel", "vote-kick-cancel", "votekick-cancel"])
    @ClientSide
    suspend fun cancelCommand(player: Player) {
        cancel(player)
    }

    override fun getRequiredVotes(): Int = 3

    override fun canPlayerStart(player: Player, objective: VoteKickVoteObjective): Boolean {
        if (Groups.player.size() < 3) {
            player.sendMessage("[#ff0000]Minimum of 3 players to start a '$name' vote.")

            return false
        }

        if (player == objective.target) {
            player.sendMessage("[#ff0000]Cannot start a '$name' vote against yourself.")

            return false
        }

        return true
    }

    override suspend fun onSuccess(session: VoteSession<VoteKickVoteObjective>) {
        val duration = 5.hours

        session.objective.target.kickWithoutLogging(
            """
            [#ff0000]You were vote kicked with the reason
            []${session.objective.reason}
            [#00ff00]You can join again in ${duration.toDisplayString()}.
            [#00ff00]Appeal in Discord.
            """.trimIndent()
        )

        Call.sendMessage("[#00ff00]Vote kick success. Kicked ${session.objective.target.plainName()} for ${duration.toDisplayString()}.")
    }

    override suspend fun getSessionDetails(session: VoteSession<VoteKickVoteObjective>): String {
        return """
            Voting to kick '${session.objective.target.plainName()}' with the reason '${session.objective.reason}'
            Type [accent]/vote y[] or [accent]/vote n[] to vote.
        """.trimIndent()
    }
}