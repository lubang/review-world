package com.github.lubang.review.world.domain.reception.fetcher

import akka.actor.Props
import com.github.lubang.review.world.core.Review

interface Fetcher {
    fun props(id: String, config: Config): Props

    interface Config

    sealed class Event {
        data class ReviewFetched(val id: String, val review: Review) : Event()
    }
}