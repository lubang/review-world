package com.github.lubang.review.world.domain.entities.streamline

interface StreamlineScheduler {

    fun schedule(streamlineId: String)

    fun cancel(streamlineId: String)

}