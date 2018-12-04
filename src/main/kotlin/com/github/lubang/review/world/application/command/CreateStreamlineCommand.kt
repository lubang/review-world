package com.github.lubang.review.world.application.command

import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import java.time.ZonedDateTime

data class CreateStreamlineCommand(val streamlineId: String,
                                   val register: String,
                                   val registeredAt: ZonedDateTime,
                                   val fetcherConfig: FetcherConfig,
                                   val notifiersConfigs: Set<NotifierConfig>) {
    init {
        if (fetcherConfig.fetchInterval < MIN_FETCH_INTERVAL) {
            val message = "Streamline `FetchInterval (${fetcherConfig.fetchInterval})`" +
                    " should be larger than 10000 ms"
            throw IllegalArgumentException(message)
        }
    }

    companion object {
        private const val MIN_FETCH_INTERVAL = 10000
    }
}