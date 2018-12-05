package com.github.lubang.review.world.domain.models.streamline

import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import java.time.ZonedDateTime

class StreamlineState {
    var streamlineId: String? = null
        private set

    var register: String? = null
        private set

    var registeredAt: ZonedDateTime? = null
        private set

    var fetcherConfig: FetcherConfig? = null
        private set

    var notifierConfigs: Set<NotifierConfig> = setOf()
        private set

    var mode: Mode = Mode.NONE
        private set

    var lastFetchedAt: ZonedDateTime? = null
        private set

    fun update(event: StreamlineEvent) {
        when (event) {
            is StreamlineEvent.Created -> {
                streamlineId = event.streamlineId
                register = event.register
                registeredAt = event.registeredAt
                fetcherConfig = event.fetcherConfig
                notifierConfigs = event.notifierConfigs
                mode = Mode.READY
            }

            is StreamlineEvent.Fetched -> {
                if (lastFetchedAt == null || event.fetchedAt.isAfter(lastFetchedAt)) {
                    lastFetchedAt = event.fetchedAt
                }
            }

            is StreamlineEvent.Destroyed -> {
                mode = Mode.DESTROYED
            }
        }
    }

    enum class Mode {
        NONE,
        READY,
        AUTO_FETCH,
        DESTROYED
    }
}
