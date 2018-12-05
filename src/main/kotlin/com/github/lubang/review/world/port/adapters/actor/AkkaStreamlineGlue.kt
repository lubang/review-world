package com.github.lubang.review.world.port.adapters.actor

import akka.actor.ActorRef
import akka.pattern.PatternsCS
import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.domain.common.Review
import com.github.lubang.review.world.domain.models.streamline.Streamline
import com.github.lubang.review.world.domain.models.streamline.StreamlineState
import com.github.lubang.review.world.port.adapters.actor.models.AkkaStreamlineActor.*
import java.util.concurrent.CompletionStage

class AkkaStreamlineGlue(private val streamlineId: String,
                         private val streamlineActor: ActorRef)
    : Streamline {

    override fun createStreamline(register: String,
                                  fetcherConfig: FetcherConfig,
                                  notifierConfigs: Set<NotifierConfig>) {
        val create = Create(streamlineId, register, fetcherConfig, notifierConfigs)
        streamlineActor.tell(create, ActorRef.noSender())
    }

    override fun fetch() {
        streamlineActor.tell(Fetch, ActorRef.noSender())
    }

    override fun notify(reviews: Set<Review>) {
        streamlineActor.tell(Notify(reviews), ActorRef.noSender())
    }

    override fun destroyStreamline() {
        streamlineActor.tell(Destroy, ActorRef.noSender())
    }

    override fun getState(): CompletionStage<StreamlineState> {
        return PatternsCS.ask(streamlineActor, GetState, DEFAULT_TIMEOUT)
                .thenApply(StreamlineState::class.java::cast)
    }

    companion object {
        private const val DEFAULT_TIMEOUT: Long = 10000
    }
}
