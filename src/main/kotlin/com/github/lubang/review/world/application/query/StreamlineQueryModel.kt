package com.github.lubang.review.world.application.query

import com.github.lubang.review.world.domain.models.streamline.StreamlineRepository
import java.util.concurrent.CompletionStage

class StreamlineQueryModel(private val streamlineRepository: StreamlineRepository) {

    fun getStreamlineStatus(streamlineId: String): CompletionStage<StreamlineInfo> {
        val streamline = streamlineRepository.get(streamlineId)
        val state = streamline.getState()
        return state.thenApply {
            StreamlineInfo(
                    it.streamlineId,
                    it.register,
                    it.registeredAt,
                    it.fetcherConfig,
                    it.notifierConfigs,
                    it.lastFetchedAt)
        }
    }

}