package com.github.lubang.review.world.infra.notifier

import akka.Done
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.domain.common.Review
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

@DisplayName("A Slack notifier")
internal class SlackNotifierTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe

    private lateinit var notifier: ActorRef

    private lateinit var slackWebhookUrl: String
    private lateinit var slackChannel: String

    @BeforeEach
    private fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)

        notifier = system.actorOf(SlackNotifier.props())

        slackWebhookUrl = System.getenv("SLACK_WEBHOOK")
        slackChannel = System.getenv("SLACK_CHANNEL")
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive a Notify command should return a Done`() {
        val review = Review(
                "streamline_id",
                "mock_review_id",
                "mock_project",
                "mock_branch",
                "mock_subject",
                "mock_owner",
                ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                ZonedDateTime.parse("2018-10-19T00:00:00Z")
        )

        val command = SlackNotifier.Command.Notify(
                SlackNotifier.Config(
                        slackWebhookUrl,
                        slackChannel),
                setOf(review))
        testProbe.send(notifier, command)

        testProbe.expectMsg(Done.done())
    }
}