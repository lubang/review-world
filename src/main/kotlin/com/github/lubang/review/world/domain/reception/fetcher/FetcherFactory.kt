package com.github.lubang.review.world.domain.reception.fetcher

import akka.actor.Props
import com.github.lubang.review.world.infra.gerrit.GerritConfig
import com.github.lubang.review.world.infra.gerrit.GerritFetcher

interface FetcherFactory {
    fun props(id: String, fetcherConfig: FetcherConfig): Props
}

class ReceptionFetcherFactory : FetcherFactory {
    override fun props(id: String, fetcherConfig: FetcherConfig): Props {
        return when (fetcherConfig) {
            is GerritConfig -> Props.create(GerritFetcher::class.java, id, fetcherConfig)
            else -> {
                val message = "Reception ID `$id` should not have a valid fetcherConfig type"
                throw IllegalArgumentException(message)
            }
        }
    }
}