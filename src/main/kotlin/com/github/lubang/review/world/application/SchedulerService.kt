package com.github.lubang.review.world.application

import akka.actor.AbstractActor
import akka.actor.Cancellable
import akka.actor.Props
import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.streamline.FetcherRouter
import com.github.lubang.review.world.domain.streamline.Streamline
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SchedulerService : AbstractActor() {
    private val schedules = mutableMapOf<String, Cancellable>()

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Streamline.Event.Started::class.java) { register(it.streamlineId, it.fetcherConfig) }
                .match(Streamline.Event.Stopped::class.java) { unregister(it.streamlineId) }
                .matchEquals(SchedulerService.Command.GetSchedules) { getSchedules() }
                .build()
    }

    override fun preStart() {
        super.preStart()
        context.system.eventStream()
                .subscribe(self, Streamline.Event::class.java)
    }

    private fun register(streamlineId: String, fetcherConfig: FetcherConfig) {
        val fetcher = context.actorOf(FetcherRouter.props())
        val cancellable = context.system.scheduler.schedule(
                Duration.Zero(),
                Duration.create(fetcherConfig.fetchInterval, TimeUnit.MILLISECONDS),
                fetcher,
                fetcherConfig,
                context.dispatcher(),
                self
        )
        schedules[streamlineId] = cancellable
    }

    private fun unregister(streamlineId: String) {
        schedules.remove(streamlineId)?.cancel()
    }

    private fun getSchedules() {
        sender.tell(schedules.keys, self)
    }

    companion object {
        fun props(): Props {
            return Props.create(SchedulerService::class.java)
        }
    }

    interface Command {
        object GetSchedules
    }
}