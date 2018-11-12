package com.github.lubang.review.world.infra

import akka.actor.Props
import com.github.lubang.review.world.domain.reception.fetcher.Fetcher
import com.github.lubang.review.world.infra.gerrit.GerritConfig
import com.github.lubang.review.world.infra.gerrit.GerritFetcher

class ReceptionFetcher : Fetcher {
    override fun props(id: String, config: Fetcher.Config): Props {
        return when (config) {
            is GerritConfig -> Props.create(GerritFetcher::class.java, id, config)
            else -> {
                val message = "Reception ID `$id` should not have a valid config"
                throw IllegalArgumentException(message)
            }
        }
    }
}