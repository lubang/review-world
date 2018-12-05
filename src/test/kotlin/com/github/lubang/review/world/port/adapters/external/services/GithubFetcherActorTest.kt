package com.github.lubang.review.world.port.adapters.external.services

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.TestPropertyHelper
import com.github.lubang.review.world.port.adapters.actor.models.AkkaStreamlineActor
import com.github.lubang.review.world.port.adapters.external.servies.GithubFetcherActor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Github fetcher")
internal class GithubFetcherActorTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe

    private lateinit var fetcher: ActorRef

    @BeforeEach
    private fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)

        fetcher = system.actorOf(GithubFetcherActor.props())
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive a Fetch should return a reviews`() {
        val command = GithubFetcherActor.Fetch(
                "streamline_id",
                GithubFetcherActor.Config(
                        10000,
                        "https://api.github.com/graphql",
                        "lubang",
                        "review-world",
                        TestPropertyHelper.githubUsername,
                        TestPropertyHelper.githubPassword
                ))
        testProbe.send(fetcher, command)

        testProbe.expectMsgClass(AkkaStreamlineActor.Notify::class.java)
    }
}