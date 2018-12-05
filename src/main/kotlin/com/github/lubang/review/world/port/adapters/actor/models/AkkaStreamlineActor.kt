package com.github.lubang.review.world.port.adapters.actor.models

import akka.actor.Props
import akka.pattern.PatternsCS
import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer
import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.domain.common.Review
import com.github.lubang.review.world.domain.models.streamline.StreamlineEvent
import com.github.lubang.review.world.domain.models.streamline.StreamlineState
import com.github.lubang.review.world.port.adapters.external.servies.GerritFetcherActor
import com.github.lubang.review.world.port.adapters.external.servies.GithubFetcherActor
import com.github.lubang.review.world.port.adapters.external.servies.SlackNotifierActor
import java.time.ZonedDateTime

class AkkaStreamlineActor(private val streamlineId: String)
    : AbstractPersistentActor() {

    private var state = StreamlineState()

    override fun persistenceId(): String {
        return streamlineId
    }

    override fun createReceiveRecover(): Receive {
        return receiveBuilder()
                .match(StreamlineEvent::class.java) { event -> state.update(event) }
                .match(SnapshotOffer::class.java) { ss -> state = ss.snapshot() as StreamlineState }
                .build()
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Create::class.java) { create(it) }
                .matchEquals(Fetch) { fetch() }
                .match(Notify::class.java) { notify(it.reviews) }
                .matchEquals(Destroy) { destroy() }
                .matchEquals(GetState) { sender.tell(state, self) }
                .build()
    }

    private fun create(message: Create) {
        val event = StreamlineEvent.Created(
                streamlineId,
                message.register,
                ZonedDateTime.now(),
                message.fetcherConfig,
                message.notifierConfigs)
        persist(event) {
            state.update(it)
            context.system().eventStream().publish(it)
        }
    }

    private fun fetch() {
        val config = state.fetcherConfig
        val response = when (config) {
            is GerritFetcherActor.Config -> PatternsCS.ask(
                    context.actorOf(GerritFetcherActor.props()),
                    GerritFetcherActor.Fetch(streamlineId, config),
                    FETCH_TIMEOUT)
            is GithubFetcherActor.Config -> PatternsCS.ask(
                    context.actorOf(GithubFetcherActor.props()),
                    GithubFetcherActor.Fetch(streamlineId, config),
                    FETCH_TIMEOUT)
            else -> throw IllegalArgumentException("Invalid fetch config `$config`")
        }
        PatternsCS.pipe(response, context.dispatcher()).to(self)

        PatternsCS.pipe(response, context.dispatcher()).run {
            val event = StreamlineEvent.Fetched(streamlineId, ZonedDateTime.now())
            persist(event) {
                state.update(event)
                context.system().eventStream().publish(event)
            }
        }
    }

    private fun notify(reviews: Set<Review>) {
        for (notifierConfig in state.notifierConfigs) {
            when (notifierConfig) {
                is SlackNotifierActor.Config -> notifyBySlack(notifierConfig, reviews)
            }
        }
    }

    private fun notifyBySlack(config: SlackNotifierActor.Config, reviews: Set<Review>) {
        val notifier = context.actorOf(SlackNotifierActor.props())

        for (review in reviews) {
            val response = PatternsCS.ask(
                    notifier,
                    SlackNotifierActor.Notify(config, review),
                    FETCH_TIMEOUT)
            PatternsCS.pipe(response, context.dispatcher()).run {
                val event = StreamlineEvent.Notified(streamlineId,
                        review,
                        "SlackNotifier",
                        ZonedDateTime.now())
                persist(event) {
                    state.update(event)
                    context.system().eventStream().publish(event)
                }
            }
        }

        context.stop(notifier)
    }

    private fun destroy() {
        val event = StreamlineEvent.Destroyed(streamlineId, ZonedDateTime.now())
        persist(event) {
            state.update(event)
            context.system().eventStream().publish(event)
            context.stop(self)
        }
    }

    companion object {
        fun props(id: String): Props {
            return Props.create(AkkaStreamlineActor::class.java, id)
        }

        private const val FETCH_TIMEOUT = 10000L
    }

    data class Create(val streamlineId: String,
                      val register: String,
                      val fetcherConfig: FetcherConfig,
                      val notifierConfigs: Set<NotifierConfig>)

    object Fetch

    data class Notify(val reviews: Set<Review>)

    object Destroy

    object GetState
}