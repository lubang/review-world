package com.github.lubang.review.world.domain.streamline

import akka.actor.Props
import akka.pattern.PatternsCS
import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer
import com.github.lubang.review.world.domain.common.DomainEvent
import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.domain.common.Review
import java.time.ZonedDateTime

class Streamline(private val streamlineId: String)
    : AbstractPersistentActor() {

    private var state = StreamlineState()

    override fun persistenceId(): String {
        return streamlineId
    }

    override fun createReceiveRecover(): Receive {
        return receiveBuilder()
                .match(Streamline.Event::class.java) { event -> state.update(event) }
                .match(SnapshotOffer::class.java) { ss -> state = ss.snapshot() as StreamlineState }
                .build()
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Command.Create::class.java) {
                    create(it.register, it.registeredAt, it.fetcherConfig, it.notifiersConfig)
                }
                .matchEquals(Command.Destroy) { destroy() }
                .matchEquals(Command.Start) { start() }
                .matchEquals(Command.GetStatus) { sender.tell(state.status, self) }
                .matchEquals(Command.Fetch) { fetch() }
                .match(Command.Notify::class.java) { notify(it.reviews) }
                .build()
    }

    private fun create(register: String,
                       registeredAt: ZonedDateTime,
                       fetcherConfig: FetcherConfig,
                       notifiersConfig: Set<NotifierConfig>) {
        val event = Event.Created(
                streamlineId,
                register,
                registeredAt,
                fetcherConfig,
                notifiersConfig)
        persist(event) {
            state.update(it)
            context.system().eventStream().publish(it)
        }
    }

    private fun destroy() {
        persist(Event.Destroyed(streamlineId)) {
            state.update(it)
            context.system().eventStream().publish(it)
        }
    }

    private fun start() {
        if (state.status != StreamlineState.Status.CREATED) {
            val event = Event.Failed(
                    streamlineId,
                    "Streamline's status (${state.status}) is invalid to start",
                    ZonedDateTime.now())
            context.system().eventStream().publish(event)
            return
        }

        persist(Event.Started(streamlineId, state.fetcherConfig!!)) {
            state.update(it)
            context.system().eventStream().publish(it)
        }
    }

    private fun fetch() {
        val fetcherRouter = context.actorOf(FetcherRouter.props())

        val response = PatternsCS.ask(
                fetcherRouter,
                FetcherRouter.Command.Fetch(streamlineId, state.fetcherConfig!!),
                FETCH_TIMEOUT)

        PatternsCS.pipe(response, context.dispatcher()).run {
            persist(Event.Fetched(streamlineId, ZonedDateTime.now())) { event ->
                state.update(event)
                context.system().eventStream().publish(event)
            }
        }
    }

    private fun notify(reviews: Set<Review>) {
        val notifierRouter = context.actorOf(NotifierRouter.props())

        val response = PatternsCS.ask(
                notifierRouter,
                NotifierRouter.Command.Notify(streamlineId, state.notifiersConfig, reviews),
                NOTIFY_TIMEOUT)

        PatternsCS.pipe(response, context.dispatcher()).run {
            val events = reviews.map { review -> Event.Notified(streamlineId, review) }
            persistAll(events) { event ->
                state.update(event)
                context.system().eventStream().publish(event)
            }
        }
    }

    companion object {
        fun props(id: String): Props {
            return Props.create(Streamline::class.java, id)
        }

        private const val FETCH_TIMEOUT: Long = 10000
        private const val NOTIFY_TIMEOUT: Long = 10000
    }

    interface Command {
        data class Create(val register: String,
                          val registeredAt: ZonedDateTime,
                          val fetcherConfig: FetcherConfig,
                          val notifiersConfig: Set<NotifierConfig>) {
            init {
                if (fetcherConfig.fetchInterval < MIN_FETCH_INTERVAL) {
                    val message = "SchedulerService `FetchInterval (${fetcherConfig.fetchInterval})`" +
                            " should be larger than 10000 ms"
                    throw IllegalArgumentException(message)
                }
            }

            companion object {
                private const val MIN_FETCH_INTERVAL = 10000
            }
        }

        object Destroy

        object Start

        object GetStatus

        object Fetch

        data class Notify(val reviews: Set<Review>)
    }

    interface Event : DomainEvent {
        data class Created(val streamlineId: String,
                           val register: String,
                           val registeredAt: ZonedDateTime,
                           val fetcherConfig: FetcherConfig,
                           val notifierConfigs: Set<NotifierConfig>) : Event

        data class Destroyed(val streamlineId: String) : Event

        data class Started(val streamlineId: String,
                           val fetcherConfig: FetcherConfig) : Event

        data class Stopped(val streamlineId: String) : Event

        data class Failed(val streamlineId: String,
                          val reason: String,
                          val failedAt: ZonedDateTime) : Event

        data class Fetched(val streamlineId: String,
                           val fetchedAt: ZonedDateTime) : Event

        data class Notified(val streamlineId: String,
                            val review: Review) : Event
    }
}
