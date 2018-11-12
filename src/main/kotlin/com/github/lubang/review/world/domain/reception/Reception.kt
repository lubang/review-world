package com.github.lubang.review.world.domain.reception

import akka.actor.Props
import akka.pattern.PatternsCS
import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer
import com.github.lubang.review.world.domain.reception.fetcher.FetcherFactory
import com.github.lubang.review.world.domain.reception.status.ReceptionStatus

class Reception(private val fetcherFactory: FetcherFactory)
    : AbstractPersistentActor() {

    private var state = ReceptionState()

    override fun persistenceId(): String {
        return RECEPTION_PERSISTENT_ID
    }

    override fun createReceiveRecover(): Receive {
        return receiveBuilder()
                .match(Reception.Event::class.java) { event -> state.update(event) }
                .match(SnapshotOffer::class.java) { ss -> state = ss.snapshot() as ReceptionState }
                .build()
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Command.AddCommand::class.java) { addCollector(it) }
                .match(Command.RemoveCommand::class.java) { removeCollector(it.id) }
                .match(Command.FetchCommand::class.java) { fetchReviews(it) }
                .matchEquals(Command.GetCollectors) { getCollectors() }
                .build()
    }

    private fun addCollector(command: Command.AddCommand) {
        persist(Event.CollectorAdded(command.id, command.config)) {
            state.update(it)
            context.system()
                    .eventStream().publish(it)
            sender.tell(Command.AddResponse(it.id, it.config, true, ""), self)
        }
    }

    private fun removeCollector(id: String) {
        persist(Event.CollectorRemoved(id)) {
            state.update(it)
            context.system()
                    .eventStream().publish(it)
            sender.tell(Command.RemoveResponse(it.id), self)
        }
    }

    private fun fetchReviews(command: Command.FetchCommand) {
        if (!state.hasRequest(command.id)) {
            val response = Command.FetchResponse(
                    command.id,
                    false,
                    "Fetch ID `${command.id}` is not exist in a reception",
                    0)
            sender.tell(response, self)
            return
        }

        val request = state.getRequest(command.id)
        val fetcher = context.actorOf(fetcherFactory.props(command.id, request.fetcher))
        val response = PatternsCS.ask(fetcher, command, 10000)
        PatternsCS.pipe(response, context.dispatcher()).to(sender)
    }

    private fun getCollectors() {
        sender.tell(Command.GetCollectorsResponse(state.getStatus()), self)
    }

    companion object {
        fun props(fetcherFactory: FetcherFactory): Props {
            return Props.create(Reception::class.java, fetcherFactory)
        }

        private const val RECEPTION_PERSISTENT_ID = "review-world-reception"
    }

    sealed class Command {
        data class AddCommand(val id: String, val config: ReceptionConfig) : Command()
        data class AddResponse(val id: String,
                               val config: ReceptionConfig,
                               val isSuccess: Boolean,
                               val reason: String)

        data class RemoveCommand(val id: String) : Command()
        data class RemoveResponse(val id: String)

        object GetCollectors : Command()
        data class GetCollectorsResponse(val status: List<ReceptionStatus>)

        data class FetchCommand(val id: String) : Command()
        data class FetchResponse(val id: String,
                                 val isSuccess: Boolean,
                                 val reason: String,
                                 val fetchedCount: Int)
    }

    sealed class Event {
        data class CollectorAdded(val id: String, val config: ReceptionConfig) : Event()
        data class CollectorRemoved(val id: String) : Event()
    }
}