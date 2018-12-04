package com.github.lubang.review.world.domain.entities.streamline

import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.domain.common.Review
import com.github.lubang.review.world.domain.event.DomainEvent
import java.time.ZonedDateTime

interface StreamlineEvent : DomainEvent {

    data class Created(val streamlineId: String,
                       val register: String,
                       val registeredAt: ZonedDateTime,
                       val fetcherConfig: FetcherConfig,
                       val notifierConfigs: Set<NotifierConfig>) : StreamlineEvent

    data class Fetched(val streamlineId: String,
                       val fetchedAt: ZonedDateTime) : StreamlineEvent

    data class Notified(val streamlineId: String,
                        val review: Review,
                        val notifierType: String,
                        val notifiedAt: ZonedDateTime) : StreamlineEvent

    data class Destroyed(val streamlineId: String,
                         val destroyedAt: ZonedDateTime) : StreamlineEvent

}