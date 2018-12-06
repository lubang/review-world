package com.github.lubang.review.world.port.adapters.serialization

import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.port.adapters.external.servies.GerritFetcherActor
import com.github.lubang.review.world.port.adapters.external.servies.GithubFetcherActor
import com.github.lubang.review.world.port.adapters.external.servies.SlackNotifierActor
import com.google.gson.GsonBuilder
import java.time.ZonedDateTime

object GsonSupport {
    val gson = GsonBuilder()
            .registerTypeAdapterFactory(
                    GsonRuntimeTypeAdapterFactory.of(FetcherConfig::class.java)
                            .registerSubtype(GerritFetcherActor.Config::class.java, "GerritFetcher")
                            .registerSubtype(GithubFetcherActor.Config::class.java, "GithubFetcher")
            )
            .registerTypeAdapterFactory(
                    GsonRuntimeTypeAdapterFactory.of(NotifierConfig::class.java)
                            .registerSubtype(SlackNotifierActor.Config::class.java, "SlackNotifier")
            )
            .registerTypeAdapter(ZonedDateTime::class.java, GsonZonedDateTimeTypeAdapter())
            .create()!!
}