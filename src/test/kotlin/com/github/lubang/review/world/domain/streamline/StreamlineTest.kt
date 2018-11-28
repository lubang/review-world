package com.github.lubang.review.world.domain.streamline

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.TestPropertyHelper
import com.github.lubang.review.world.domain.common.Review
import com.github.lubang.review.world.infra.fetcher.GerritFetcher
import com.github.lubang.review.world.infra.fetcher.GithubFetcher
import com.github.lubang.review.world.infra.notifier.SlackNotifier
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import scala.concurrent.duration.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@DisplayName("A streamline")
internal class StreamlineTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe
    private lateinit var eventSubscriber: TestProbe

    private lateinit var streamline: ActorRef

    @BeforeEach
    private fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)
        eventSubscriber = TestProbe(system)

        streamline = system.actorOf(Streamline.props("streamline_id"))

        system.eventStream().subscribe(eventSubscriber.ref(), Streamline.Event::class.java)
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive an Add command should raise a Created event and update a state to CREATED`() {
        val command = Streamline.Command.Create(
                "lubang",
                ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                GithubFetcher.Config(
                        10000,
                        "https://api.github.com/graphql",
                        "lubang",
                        "review-world",
                        TestPropertyHelper.githubUsername,
                        TestPropertyHelper.githubPassword
                ),
                setOf(SlackNotifier.Config("https://webhook.slack.com/19284010", "#notify"))
        )
        testProbe.send(streamline, command)
        eventSubscriber.expectMsg(Streamline.Event.Created("streamline_id",
                command.register,
                command.registeredAt,
                command.fetcherConfig,
                command.notifiersConfig))

        testProbe.send(streamline, Streamline.Query.GetStatus)
        testProbe.expectMsg(StreamlineState.Status.CREATED)
    }

    @Test
    fun `receive a Remove command should raise a Destroyed event and update a state to DESTROYED`() {
        testProbe.send(streamline, Streamline.Command.Destroy)
        eventSubscriber.expectMsg(Streamline.Event.Destroyed("streamline_id"))

        testProbe.send(streamline, Streamline.Query.GetStatus)
        testProbe.expectMsg(StreamlineState.Status.DESTROYED)
    }

    @Test
    fun `receive a Start command should validate a CREATED state and raise a Failed event when it's invalid`() {
        testProbe.send(streamline, Streamline.Command.Start)

        val actual = eventSubscriber.expectMsgClass(Streamline.Event.Failed::class.java)
        assertEquals("streamline_id", actual.streamlineId)
        assertEquals("Streamline's status (NONE) is invalid to start", actual.reason)
    }

    @Test
    fun `receive a Start command should raise a Started event, update a state to STARTED and send a Register command to a scheduler`() {
        `receive an Add command should raise a Created event and update a state to CREATED`()

        testProbe.send(streamline, Streamline.Command.Start)

        eventSubscriber.expectMsg(Streamline.Event.Started(
                "streamline_id",
                GithubFetcher.Config(
                        10000,
                        "https://api.github.com/graphql",
                        "lubang",
                        "review-world",
                        TestPropertyHelper.githubUsername,
                        TestPropertyHelper.githubPassword
                )))

        testProbe.send(streamline, Streamline.Query.GetStatus)
        testProbe.expectMsg(StreamlineState.Status.STARTED)
    }

    @Test
    fun `receive a Fetch command should raise a Fetched event and update last fetched at in the state`() {
        `receive an Add command should raise a Created event and update a state to CREATED`()

        testProbe.send(streamline, Streamline.Command.Fetch)

        val actualFetchedEvent = eventSubscriber.expectMsgClass(
                Duration.create(10, TimeUnit.SECONDS),
                Streamline.Event.Fetched::class.java)
        assertEquals("streamline_id", actualFetchedEvent.streamlineId)
    }

    @Test
    fun `receive a Notify command should raise a Notified event`() {
        `receive an Add command should raise a Created event and update a state to CREATED`()

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
        testProbe.send(streamline, Streamline.Command.Notify(setOf(review)))

        val actualNotifiedEvent = eventSubscriber.expectMsgClass(
                Duration.create(10, TimeUnit.SECONDS),
                Streamline.Event.Notified::class.java)
        assertEquals("streamline_id", actualNotifiedEvent.streamlineId)
        assertEquals(review, actualNotifiedEvent.review)
    }

    @Test
    fun `add command's fetchInterval is smaller than 10000 should throw IllegalArgumentException`() {
        val actual = assertThrows(IllegalArgumentException::class.java) {
            Streamline.Command.Create(
                    "lubang",
                    ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                    GerritFetcher.Config(
                            1000,
                            "https://gerrit.url",
                            "review-world",
                            "lubang",
                            "password"),
                    setOf(SlackNotifier.Config("https://webhook.slack.com/19284010", "#notify"))
            )
        }

        assertEquals("SchedulerService `FetchInterval (1000)` should be larger than 10000 ms", actual.message)
    }

    @Test
    fun `receive a GetLastFetchedAt query should return a last fetched datetime`() {
        `receive an Add command should raise a Created event and update a state to CREATED`()
        testProbe.send(streamline, Streamline.Command.Fetch)

        testProbe.send(streamline, Streamline.Query.GetLastFetchedAt)

        testProbe.expectMsgClass(ZonedDateTime::class.java)
    }
}