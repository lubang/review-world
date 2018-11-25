package com.github.lubang.review.world.infra.fetcher

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.domain.common.Review
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("A Github fetcher")
internal class GithubFetcherTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe

    private lateinit var fetcher: ActorRef

    private lateinit var githubUsername: String
    private lateinit var githubPassword: String

    @BeforeEach
    private fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)

        fetcher = system.actorOf(GithubFetcher.props())

        githubUsername = System.getenv("GITHUB_USER")
        githubPassword = System.getenv("GITHUB_PW")
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive a Fetch command should return a reviews`() {
        val command = GithubFetcher.Command.FetchGithub(
                "streamline_id",
                GithubFetcher.Config(
                        10000,
                        "https://api.github.com/graphql",
                        "lubang",
                        "review-world",
                        githubUsername,
                        githubPassword
                ))
        testProbe.send(fetcher, command)

        val actual = testProbe.expectMsgClass(Set::class.java)
        for (actualReview in actual) {
            assertTrue(actualReview is Review)
        }
    }
}