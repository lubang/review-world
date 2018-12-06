package com.github.lubang.review.world.application.query

import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import java.time.ZonedDateTime

data class StreamlineInfo(val streamlineId: String?,
                          val register: String?,
                          val registeredAt: ZonedDateTime?,
                          val fetcherConfig: FetcherConfig?,
                          val notifierConfigs: Set<NotifierConfig>,
                          val lastFetchedAt: ZonedDateTime?)