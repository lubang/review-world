package com.github.lubang.review.world.infra.fetcher

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.TestPropertyHelper
import com.github.lubang.review.world.domain.common.Review
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("A Gerrit fetcher")
internal class GerritFetcherTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe

    private lateinit var fetcher: ActorRef

    @BeforeEach
    private fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)

        fetcher = system.actorOf(GerritFetcher.props())
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive a Fetch command should return a reviews`() {
        val command = GerritFetcher.Command.FetchGerrit(
                "streamline_id",
                GerritFetcher.Config(
                        10000,
                        TestPropertyHelper.gerritUrl,
                        TestPropertyHelper.gerritProject,
                        TestPropertyHelper.gerritUsername,
                        TestPropertyHelper.gerritPassword
                ))
        testProbe.send(fetcher, command)

        val actual = testProbe.expectMsgClass(Set::class.java)
        for (actualReview in actual) {
            assertTrue(actualReview is Review)
        }
    }
}