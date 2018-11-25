package com.github.lubang.review.world.domain.streamline

import akka.actor.AbstractActor
import akka.actor.Props
import akka.pattern.PatternsCS
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.domain.common.Review
import com.github.lubang.review.world.infra.notifier.SlackNotifier

class NotifierRouter : AbstractActor() {
    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Command.Notify::class.java) { notify(it.notifierConfigs, it.reviews) }
                .build()
    }

    private fun notify(configs: Set<NotifierConfig>,
                       reviews: Set<Review>) {
        for (config in configs) {
            when (config) {
                is SlackNotifier.Config -> notifyToSlack(config, reviews)
            }
        }
    }

    private fun notifyToSlack(config: SlackNotifier.Config,
                              reviews: Set<Review>) {
        val fetcher = context.actorOf(SlackNotifier.props())
        val response = PatternsCS.ask(
                fetcher,
                SlackNotifier.Command.Notify(config, reviews),
                10000)
        PatternsCS.pipe(response, context.dispatcher()).to(sender)
    }

    companion object {
        fun props(): Props {
            return Props.create(NotifierRouter::class.java)
        }
    }

    interface Command {
        data class Notify(val streamlineId: String,
                          val notifierConfigs: Set<NotifierConfig>,
                          val reviews: Set<Review>)
    }
}