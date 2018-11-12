package com.github.lubang.review.world.domain.reception

import com.github.lubang.review.world.domain.reception.fetcher.FetcherConfig
import com.github.lubang.review.world.infra.slack.NotifierEngine
import java.time.ZonedDateTime

data class ReceptionConfig(val register: String,
                           val registeredAt: ZonedDateTime,
                           val fetchInterval: Long,
                           val fetcher: FetcherConfig,
                           val notifier: NotifierEngine) {

    companion object {
        private const val MIN_FETCH_INTERVAL = 10000
    }

    init {
        if (fetchInterval < MIN_FETCH_INTERVAL) {
            val message = "ReceptionConfig `fetchInterval` should be larger than $MIN_FETCH_INTERVAL millis"
            throw IllegalArgumentException(message)
        }
    }
}