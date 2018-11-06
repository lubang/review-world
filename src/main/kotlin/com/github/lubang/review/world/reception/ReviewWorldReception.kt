package com.github.lubang.review.world.reception

import akka.actor.Props
import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer
import com.github.lubang.review.world.notifier.NotifierEngine
import com.github.lubang.review.world.review.ReviewEngine
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

        data class StartCollector(val collectorId: String)

        object GetCollectors

        object Shutdown
    }

    sealed class Event : Serializable {
        data class CollectorAdded(val collectorId: String,
                                  val register: String,
                                  val registeredAt: ZonedDateTime,
                                  val reviewEngine: ReviewEngine.Gerrit,
                                  val notifierEngine: NotifierEngine.Slack) : Event()

        data class CollectorRemoved(val collectorId: String) : Event()

        data class CollectorStateChanged(val collectorId: String,
                                         val status: ReviewCollectorInfo.Status) : Event()
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
                .match(Command.StartCollector::class.java) { onStartCollector(it) }
                .match(Command.Shutdown::class.java) { context.stop(self) }
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

    private fun onStartCollector(cmd: Command.StartCollector) {
        if (!state.hasCollector(cmd.collectorId)) {
            return
        }
        if (state.getCollector(cmd.collectorId)?.status == ReviewCollectorInfo.Status.RUNNING) {
            return
        }

        val event = Event.CollectorStateChanged(cmd.collectorId, ReviewCollectorInfo.Status.RUNNING)
        persist(event) { evt: Event ->
            state.update(evt)
            context.system().eventStream().publish(evt)
        }
    }
}