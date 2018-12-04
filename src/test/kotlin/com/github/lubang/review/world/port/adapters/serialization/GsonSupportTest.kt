package com.github.lubang.review.world.port.adapters.serialization

import com.github.lubang.review.world.port.adapters.external.servies.GithubFetcherActor
import com.github.lubang.review.world.port.adapters.external.servies.SlackNotifierActor
import com.github.lubang.review.world.port.adapters.web.AkkaStreamlineController
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("GSON support")
internal class GsonSupportTest {
    @Test
    fun `should serialize a polymorphism instance to a json with it's type`() {
        val request = AkkaStreamlineController.CreateStreamlineRequest("lubang",
                GithubFetcherActor.Config(10000,
                        "gith",
                        "repo owner",
                        "repo name",
                        "user name",
                        "user pw"),
                setOf(SlackNotifierActor.Config("webhook url", "#channel")))

        val json = GsonSupport.gson.toJson(request)

        assertEquals("{\"register\":\"lubang\",\"fetcherConfig\":{\"@type\":\"GithubFetcher\",\"fetchInterval\":10000,\"githubGraphQlUrl\":\"gith\",\"owner\":\"repo owner\",\"repository\":\"repo name\",\"username\":\"user name\",\"password\":\"user pw\"},\"notifiersConfigs\":[{\"@type\":\"SlackNotifier\",\"webhookUrl\":\"webhook url\",\"channel\":\"#channel\"}]}", json)
    }
}