package com.github.lubang.review.world.reception

import akka.actor.Props
import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer
import com.github.lubang.review.world.collector.ReviewEngine
import com.github.lubang.review.world.notifier.NotifierEngine
import java.io.Serializable
import java.time.ZonedDateTime


class ReviewWorldReception : AbstractPersistentActor() {

    companion object {
        fun props(): Props {
            return Props.create(ReviewWorldReception::class.java)
        }
    }

    sealed class Command {
        data class AddCollector(val collectorId: String,
                                val register: String,
                                val registeredAt: ZonedDateTime,
                                val reviewEngine: ReviewEngine.Gerrit,
                                val notifierEngine: NotifierEngine.Slack) : Command()

        data class RemoveCollector(val collectorId: String)

        object GetCollectors
    }

    sealed class Event : Serializable {
        data class CollectorAdded(val collectorId: String,
                                  val register: String,
                                  val registeredAt: ZonedDateTime,
                                  val reviewEngine: ReviewEngine.Gerrit,
                                  val notifierEngine: NotifierEngine.Slack) : Event()

        data class CollectorRemoved(val collectorId: String) : Event()
    }

    private var state = ReviewWorldReceptionState()

    override fun persistenceId(): String {
        return "review-world-reception"
    }

    override fun createReceiveRecover(): Receive {
        return receiveBuilder()
                .match(ReviewWorldReception.Event::class.java) { event -> state.update(event) }
                .match(SnapshotOffer::class.java) { ss -> state = ss.snapshot() as ReviewWorldReceptionState }
                .build()
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Command.GetCollectors::class.java) { onGetCollectors() }
                .match(Command.AddCollector::class.java) { onAddCollector(it) }
                .match(Command.RemoveCollector::class.java) { onRemoveCollector(it) }
                .build()
    }

    private fun onGetCollectors() {
        val collectors = state.getCollectors()
        sender.tell(collectors, self)
    }

    private fun onAddCollector(cmd: Command.AddCollector) {
        if (state.hasCollector(cmd.collectorId)) {
            return
        }

        val event = Event.CollectorAdded(
                cmd.collectorId,
                cmd.register,
                cmd.registeredAt,
                cmd.reviewEngine,
                cmd.notifierEngine)
        persist(event) { evt: Event ->
            state.update(evt)
            context.system().eventStream().publish(evt)
        }
    }

    private fun onRemoveCollector(cmd: Command.RemoveCollector) {
        if (!state.hasCollector(cmd.collectorId)) {
            return
        }

        val event = Event.CollectorRemoved(cmd.collectorId)
        persist(event) { evt: Event ->
            state.update(evt)
            context.system().eventStream().publish(evt)
        }
    }
}