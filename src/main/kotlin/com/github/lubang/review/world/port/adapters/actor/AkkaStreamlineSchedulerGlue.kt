package com.github.lubang.review.world.port.adapters.actor

import akka.actor.ActorRef
import com.github.lubang.review.world.domain.entities.streamline.StreamlineScheduler
import com.github.lubang.review.world.port.adapters.actor.model.AkkaStreamlineSchedulerActor

class AkkaStreamlineSchedulerGlue(private val schedulerActor: ActorRef) : StreamlineScheduler {

    override fun schedule(streamlineId: String) {
        val startAutoFetch = AkkaStreamlineSchedulerActor.StartAutoFetch(streamlineId)
        schedulerActor.tell(startAutoFetch, ActorRef.noSender())
    }

    override fun cancel(streamlineId: String) {
        val stopAutoFetch = AkkaStreamlineSchedulerActor.StopAutoFetch(streamlineId)
        schedulerActor.tell(stopAutoFetch, ActorRef.noSender())

    }

}