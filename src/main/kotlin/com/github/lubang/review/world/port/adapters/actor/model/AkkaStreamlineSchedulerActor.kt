package com.github.lubang.review.world.port.adapters.actor.model

import akka.actor.AbstractActor
import akka.actor.Cancellable
import akka.actor.Props
import com.github.lubang.review.world.domain.entities.streamline.StreamlineEvent
import com.github.lubang.review.world.domain.entities.streamline.StreamlineRepository
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class AkkaStreamlineSchedulerActor(private val repository: StreamlineRepository)
    : AbstractActor() {

    private val schedules = mutableMapOf<String, Cancellable>()

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(StartAutoFetch::class.java) { startAutoFetch(it.streamlineId) }
                .match(StopAutoFetch::class.java) { stopAutoFetch(it.streamlineId) }
                .match(Schedule::class.java) { schedule(it.streamlineId) }
                .build()
    }

    override fun preStart() {
        super.preStart()
        context.system.eventStream().subscribe(self, StreamlineEvent::class.java)
    }

    private fun startAutoFetch(streamlineId: String) {
        val streamline = repository.getById(streamlineId)
        streamline.getState().thenApply {
            val fetchInterval = it.fetcherConfig!!.fetchInterval
            val cancellable = context.system.scheduler.schedule(
                    Duration.Zero(),
                    Duration.create(fetchInterval, TimeUnit.MILLISECONDS),
                    self,
                    Schedule(streamlineId),
                    context.dispatcher(),
                    self
            )
            schedules[streamlineId] = cancellable
        }
    }

    private fun stopAutoFetch(streamlineId: String) {
        schedules.remove(streamlineId)?.cancel()
    }

    private fun schedule(streamlineId: String) {
        val streamline = repository.getById(streamlineId)
        streamline.fetch()
    }

    companion object {
        fun props(repository: StreamlineRepository): Props {
            return Props.create(AkkaStreamlineSchedulerActor::class.java, repository)
        }
    }

    data class StartAutoFetch(val streamlineId: String)

    data class StopAutoFetch(val streamlineId: String)

    private class Schedule(val streamlineId: String)
}