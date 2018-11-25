package com.github.lubang.review.world.domain.streamline

import akka.actor.AbstractActor
import akka.actor.Props
import akka.pattern.PatternsCS
import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.infra.fetcher.GerritFetcher
import com.github.lubang.review.world.infra.fetcher.GithubFetcher

class FetcherRouter : AbstractActor() {
    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Command.Fetch::class.java) {
                    when (it.fetcherConfig) {
                        is GerritFetcher.Config -> fetchFromGerrit(it.streamlineId, it.fetcherConfig)
                        is GithubFetcher.Config -> fetchFromGithub(it.streamlineId, it.fetcherConfig)
                    }
                }
                .build()
    }

    private fun fetchFromGerrit(streamlineId: String, config: GerritFetcher.Config) {
        val fetcher = context.actorOf(GerritFetcher.props())
        val command = GerritFetcher.Command.FetchGerrit(streamlineId, config)
        val response = PatternsCS.ask(fetcher, command, 10000)
        PatternsCS.pipe(response, context.dispatcher()).to(sender)
    }

    private fun fetchFromGithub(streamlineId: String, config: GithubFetcher.Config) {
        val fetcher = context.actorOf(GithubFetcher.props())
        val command = GithubFetcher.Command.FetchGithub(streamlineId, config)
        val response = PatternsCS.ask(fetcher, command, 10000)
        PatternsCS.pipe(response, context.dispatcher()).to(sender)
    }

    companion object {
        fun props(): Props {
            return Props.create(FetcherRouter::class.java)
        }
    }

    interface Command {
        data class Fetch(val streamlineId: String,
                         val fetcherConfig: FetcherConfig)
    }
}