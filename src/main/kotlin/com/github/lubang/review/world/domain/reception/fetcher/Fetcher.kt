package com.github.lubang.review.world.domain.reception.fetcher

import akka.actor.Props

interface Fetcher {
    fun props(id: String, config: Config): Props

    interface Config
}