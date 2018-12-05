package com.github.lubang.review.world.domain.models.streamline

import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.domain.common.Review
import java.util.concurrent.CompletionStage

interface Streamline {

    fun createStreamline(register: String,
                         fetcherConfig: FetcherConfig,
                         notifierConfigs: Set<NotifierConfig>)

    fun fetch()

    fun notify(reviews: Set<Review>)

    fun destroyStreamline()

    fun getState(): CompletionStage<StreamlineState>

}