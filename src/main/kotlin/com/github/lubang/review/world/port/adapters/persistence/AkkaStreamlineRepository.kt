package com.github.lubang.review.world.port.adapters.persistence

import com.github.lubang.review.world.domain.entities.streamline.Streamline
import com.github.lubang.review.world.domain.entities.streamline.StreamlineRepository
import com.github.lubang.review.world.port.adapters.actor.AkkaStreamlineGlue
import com.github.lubang.review.world.port.adapters.actor.AkkaSupport
import com.github.lubang.review.world.port.adapters.actor.model.AkkaStreamlineActor

class AkkaStreamlineRepository : StreamlineRepository {
    private val system = AkkaSupport.system

    private val actorCache = mutableMapOf<String, AkkaStreamlineGlue>()

    override fun existById(streamlineId: String): Boolean {
        val streamline = getById(streamlineId)
        val state = streamline.getState().toCompletableFuture().get()
        return state.isNormal()
    }

    override fun create(streamlineId: String): Streamline {
        val actor = system.actorOf(AkkaStreamlineActor.props(streamlineId), streamlineId)
        val streamline = AkkaStreamlineGlue(streamlineId, actor)
        actorCache[streamlineId] = streamline
        return streamline
    }

    override fun getById(streamlineId: String): Streamline {
        if (actorCache.containsKey(streamlineId)) {
            return actorCache[streamlineId]!!
        }
        return create(streamlineId)
    }

    override fun delete(streamlineId: String) {
        val streamline = getById(streamlineId)
        streamline.destroyStreamline()
        actorCache.remove(streamlineId)
    }
}