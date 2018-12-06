package com.github.lubang.review.world.port.adapters.actor.models

import akka.actor.Props
import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer
import com.github.lubang.review.world.domain.models.streamline.StreamlineEvent

class AkkaStreamlineLifecycleActor : AbstractPersistentActor() {
    private var state = State()

    override fun persistenceId(): String {
        return "streamline-life-management"
    }

    override fun createReceiveRecover(): Receive {
        return receiveBuilder()
                .match(StreamlineEvent.Created::class.java) { event -> state.update(event) }
                .match(StreamlineEvent.Destroyed::class.java) { event -> state.update(event) }
                .match(SnapshotOffer::class.java) { ss -> state = ss.snapshot() as State }
                .build()
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Exist::class.java) { sender.tell(state.isExistStreamline(it.streamlineId), self) }
                .match(StreamlineEvent.Created::class.java) { event -> state.update(event) }
                .match(StreamlineEvent.Destroyed::class.java) { event -> state.update(event) }
                .build()
    }

    companion object {
        fun props(): Props {
            return Props.create(AkkaStreamlineLifecycleActor::class.java)
        }
    }

    data class Exist(val streamlineId: String)

    class State {
        private var streamlineIds = mutableSetOf<String>()

        fun update(event: StreamlineEvent.Created) {
            streamlineIds.add(event.streamlineId)
        }

        fun update(event: StreamlineEvent.Destroyed) {
            streamlineIds.remove(event.streamlineId)
        }

        fun isExistStreamline(streamlineId: String): Boolean {
            return streamlineIds.contains(streamlineId)
        }
    }
}