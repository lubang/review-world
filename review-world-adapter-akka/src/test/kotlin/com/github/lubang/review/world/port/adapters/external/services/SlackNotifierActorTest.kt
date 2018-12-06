package com.github.lubang.review.world.port.adapters.external.services

import akka.Done
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.TestPropertyHelper
import com.github.lubang.review.world.domain.common.Review
import com.github.lubang.review.world.port.adapters.external.servies.SlackNotifierActor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

@DisplayName("Slack notifier")
internal class SlackNotifierActorTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe

    private lateinit var notifier: ActorRef

    @BeforeEach
    private fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)

        notifier = system.actorOf(SlackNotifierActor.props())
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive a Notify should return a Done`() {
        val review = Review(
                "streamline_id",
                "mock_review_id",
                "mock_project",
                "mock_branch",
                "mock_subject",
                "mock_owner",
                "review_url",
                ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                ZonedDateTime.parse("2018-10-19T00:00:00Z")
        )

        val message = SlackNotifierActor.Notify(
                SlackNotifierActor.Config(
                        TestPropertyHelper.slackWebhook,
                        TestPropertyHelper.slackChannel),
                review)
        testProbe.send(notifier, message)

        testProbe.expectMsg(Done.done())
    }
}