package com.github.lubang.review.world.reception

import com.github.lubang.review.world.notifier.NotifierEngine
import com.github.lubang.review.world.review.ReviewEngine
import java.time.ZonedDateTime

data class ReviewCollectorInfo(val collectorId: String,
                               var status: Status,
                               val register: String,
                               val registeredAt: ZonedDateTime,
                               val reviewEngine: ReviewEngine,
                               val notifierEngine: NotifierEngine) {
    enum class Status {
        READY, RUNNING,
    }
}