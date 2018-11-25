package com.github.lubang.review.world.domain.streamline

import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import java.time.ZonedDateTime

class StreamlineState {
    private var streamlineId: String? = null

    private var register: String? = null

    private var registeredAt: ZonedDateTime? = null

    var fetcherConfig: FetcherConfig? = null
        private set

    var notifiersConfig: Set<NotifierConfig> = setOf()
        private set

    var status: Status = Status.NONE
        private set

    var lastFetchedAt: ZonedDateTime? = null
        private set

    fun update(event: Streamline.Event) {
        when (event) {
            is Streamline.Event.Created -> {
                streamlineId = event.streamlineId
                register = event.register
                registeredAt = event.registeredAt
                fetcherConfig = event.fetcherConfig
                notifiersConfig = event.notifierConfigs
                status = Status.CREATED
            }

            is Streamline.Event.Destroyed -> {
                status = Status.DESTROYED
            }

            is Streamline.Event.Started -> {
                status = Status.STARTED
            }

            is Streamline.Event.Fetched -> {
                lastFetchedAt = event.fetchedAt
            }
        }
    }

    enum class Status {
        NONE,
        CREATED,
        STARTED,
        DESTROYED
    }

}
