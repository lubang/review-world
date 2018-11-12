package com.github.lubang.review.world.domain.reception

import com.github.lubang.review.world.domain.reception.fetcher.Fetcher
import com.github.lubang.review.world.domain.reception.notifier.Notifier
import java.time.ZonedDateTime

data class ReceptionConfig(val register: String,
                           val registeredAt: ZonedDateTime,
                           val fetchInterval: Long,
                           val fetcher: Fetcher.Config,
                           val notifier: Notifier.Config) {

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