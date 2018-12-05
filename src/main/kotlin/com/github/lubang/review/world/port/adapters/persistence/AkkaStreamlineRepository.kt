package com.github.lubang.review.world.port.adapters.persistence

import akka.actor.ActorRef
import akka.pattern.PatternsCS
import com.github.lubang.review.world.domain.models.streamline.Streamline
import com.github.lubang.review.world.domain.models.streamline.StreamlineEvent
import com.github.lubang.review.world.domain.models.streamline.StreamlineRepository
import com.github.lubang.review.world.port.adapters.actor.AkkaStreamlineGlue
import com.github.lubang.review.world.port.adapters.actor.AkkaSupport
import com.github.lubang.review.world.port.adapters.actor.models.AkkaStreamlineActor
import com.github.lubang.review.world.port.adapters.actor.models.AkkaStreamlineLifecycleActor
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache


class AkkaStreamlineRepository : StreamlineRepository {
    private val system = AkkaSupport.system

    private val lifecycleActor: ActorRef
    private val glueCache: LoadingCache<String, AkkaStreamlineGlue>

    init {
        lifecycleActor = system.actorOf(AkkaStreamlineLifecycleActor.props())
        system.eventStream().subscribe(lifecycleActor, StreamlineEvent.Created::class.java)
        system.eventStream().subscribe(lifecycleActor, StreamlineEvent.Destroyed::class.java)

        glueCache = CacheBuilder.newBuilder()
                .maximumSize(3000)
                .build(object : CacheLoader<String, AkkaStreamlineGlue>() {
                    override fun load(key: String): AkkaStreamlineGlue = createStreamlineActor(key)
                })
    }

    override fun existById(streamlineId: String): Boolean {
        val response = PatternsCS.ask(lifecycleActor, AkkaStreamlineLifecycleActor.Exist(streamlineId), 1000)
                .toCompletableFuture()
                .get()
        return response as Boolean
    }

    override fun create(streamlineId: String): Streamline {
        return glueCache.get(streamlineId)
    }

    override fun getById(streamlineId: String): Streamline {
        return glueCache.get(streamlineId)
    }

    override fun delete(streamlineId: String) {
        val streamline = getById(streamlineId)
        streamline.destroyStreamline()
        glueCache.invalidate(streamlineId)
    }

    private fun createStreamlineActor(streamlineId: String): AkkaStreamlineGlue {
        val actor = system.actorOf(AkkaStreamlineActor.props(streamlineId), streamlineId)
        return AkkaStreamlineGlue(streamlineId, actor)
    }
}