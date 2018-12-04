package com.github.lubang.review.world.specs

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.TestPropertyHelper
import com.github.lubang.review.world.application.command.CreateStreamlineCommand
import com.github.lubang.review.world.domain.common.Review
import com.github.lubang.review.world.domain.entities.streamline.StreamlineEvent
import com.github.lubang.review.world.domain.entities.streamline.StreamlineRepository
import com.github.lubang.review.world.domain.event.DomainEvent
import com.github.lubang.review.world.domain.event.DomainEventBus
import com.github.lubang.review.world.domain.event.DomainEventSubscriber
import com.github.lubang.review.world.port.adapters.actor.AkkaSupport
import com.github.lubang.review.world.port.adapters.external.servies.GerritFetcherActor
import com.github.lubang.review.world.port.adapters.external.servies.GithubFetcherActor
import com.github.lubang.review.world.port.adapters.external.servies.SlackNotifierActor
import com.github.lubang.review.world.port.adapters.messaging.AkkaDomainEventBus
import com.github.lubang.review.world.port.adapters.messaging.AkkaDomainEventSubscriber
import com.github.lubang.review.world.port.adapters.persistence.AkkaStreamlineRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.ZonedDateTime

@DisplayName("Streamline")
internal class StreamlineSpec {
    private lateinit var testProbe: TestProbe

    private lateinit var repository: StreamlineRepository
    private lateinit var eventBus: DomainEventBus
    private lateinit var subscriber: DomainEventSubscriber

    @BeforeEach
    private fun setup() {
        AkkaSupport.initialize(ActorSystem.create("ReviewWorld-Test"))

        testProbe = TestProbe(AkkaSupport.system)
        repository = AkkaStreamlineRepository()
        eventBus = AkkaDomainEventBus()
        subscriber = AkkaDomainEventSubscriber(testProbe.ref())

        eventBus.subscribe(subscriber, DomainEvent::class.java)
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(AkkaSupport.system)
    }

    @Test
    fun `create a streamline should raise a Created event`() {
        val streamlineId = "test_id"
        val streamline = repository.create(streamlineId)
        streamline.createStreamline("lubang",
                GithubFetcherActor.Config(
                        10000,
                        "https://api.github.com/graphql",
                        "lubang",
                        "review-world",
                        TestPropertyHelper.githubUsername,
                        TestPropertyHelper.githubPassword
                ),
                setOf(SlackNotifierActor.Config("https://webhook.slack.com/19284010", "#notify")))

        val actual = testProbe.expectMsgClass(StreamlineEvent.Created::class.java)
        assertEquals("test_id", actual.streamlineId)
    }

    @Test
    fun `create a streamline that fetchInterval is smaller than 10000 should throw IllegalArgumentException`() {
        val actual = Assertions.assertThrows(IllegalArgumentException::class.java) {
            CreateStreamlineCommand(
                    "test-id",
                    "lubang",
                    ZonedDateTime.now(),
                    GerritFetcherActor.Config(
                            1000,
                            "https://gerrit.url",
                            "review-world",
                            "lubang",
                            "password"),
                    setOf(SlackNotifierActor.Config("https://webhook.slack.com/19284010", "#notify"))
            )
        }

        assertEquals("Streamline `FetchInterval (1000)` should be larger than 10000 ms", actual.message)
    }

    @Test
    fun `fetch should raise a Fetched event`() {
        `create a streamline should raise a Created event`()

        val streamlineId = "test_id"
        val streamline = repository.getById(streamlineId)
        streamline.fetch()

        val actual = testProbe.expectMsgClass(StreamlineEvent.Fetched::class.java)
        assertEquals("test_id", actual.streamlineId)
    }

    @Test
    fun `notify should raise a Notified event`() {
        `create a streamline should raise a Created event`()

        val review = Review(
                "test_id",
                "mock_review_id",
                "mock_project",
                "mock_branch",
                "mock_subject",
                "mock_owner",
                "review_url",
                ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                ZonedDateTime.parse("2018-10-19T00:00:00Z"))
        val streamlineId = "test_id"
        val streamline = repository.getById(streamlineId)
        streamline.notify(setOf(review))

        val actual = testProbe.expectMsgClass(StreamlineEvent.Notified::class.java)
        assertEquals("test_id", actual.streamlineId)
        assertEquals(review, actual.review)
    }

    @Test
    fun `destroy should raise a Destroyed event`() {
        `create a streamline should raise a Created event`()

        val streamlineId = "test_id"
        repository.delete(streamlineId)

        val actual = testProbe.expectMsgClass(StreamlineEvent.Destroyed::class.java)
        assertEquals("test_id", actual.streamlineId)
    }
}