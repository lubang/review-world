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
    fun `has a gson mapper that should serialize a polymorphism instance to a json`() {
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

    @Test
    fun `has a gson mapper that should deserialize a polymorphism json to an instance`() {
        val json = "{\"register\":\"lubang\",\"fetcherConfig\":{\"@type\":\"GithubFetcher\",\"fetchInterval\":10000,\"githubGraphQlUrl\":\"gith\",\"owner\":\"repo owner\",\"repository\":\"repo name\",\"username\":\"user name\",\"password\":\"user pw\"},\"notifiersConfigs\":[{\"@type\":\"SlackNotifier\",\"webhookUrl\":\"webhook url\",\"channel\":\"#channel\"}]}"

        val actual = GsonSupport.gson.fromJson(json, AkkaStreamlineController.CreateStreamlineRequest::class.java)
        assertEquals(AkkaStreamlineController.CreateStreamlineRequest("lubang",
                GithubFetcherActor.Config(10000,
                        "gith",
                        "repo owner",
                        "repo name",
                        "user name",
                        "user pw"),
                setOf(SlackNotifierActor.Config("webhook url", "#channel"))), actual)
    }
}