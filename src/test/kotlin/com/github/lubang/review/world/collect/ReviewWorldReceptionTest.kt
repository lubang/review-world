package com.github.lubang.review.world.collect

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.pattern.Patterns
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import akka.util.Timeout
import com.github.lubang.review.world.collector.ReviewEngine
import com.github.lubang.review.world.notifier.NotifierEngine
import com.github.lubang.review.world.reception.ReviewCollectorInfo
import com.github.lubang.review.world.reception.ReviewWorldReception
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import scala.concurrent.Await
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@DisplayName("A review world reception ")
class ReviewWorldReceptionTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe
    private lateinit var reception: ActorRef
    private lateinit var addCommand: ReviewWorldReception.Command.AddCollector

    @BeforeEach
    fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)
        system.eventStream().subscribe(
                testProbe.ref(),
                ReviewWorldReception.Event::class.java)

        reception = system.actorOf(ReviewWorldReception.props())

        addCommand = ReviewWorldReception.Command.AddCollector(
                "unique_id",
                "register_name",
                ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                ReviewEngine.Gerrit(
                        "gerrit_url",
                        "project",
                        "username",
                        "password"
                ),
                NotifierEngine.Slack("webhookUrl", "channel"))
    }

    @AfterEach
    fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive an add, remove collector command should raise events`() {
        reception.tell(addCommand, ActorRef.noSender())

        val actual = testProbe.expectMsgClass(ReviewWorldReception.Event.CollectorAdded::class.java)
        assertEquals("unique_id", actual.collectorId)
        assertEquals("register_name", actual.register)
        assertTrue(ZonedDateTime.parse("2018-10-19T00:00:00Z").isEqual(actual.registeredAt))
        assertEquals("gerrit_url", actual.reviewEngine.url)
        assertEquals("project", actual.reviewEngine.project)
        assertEquals("username", actual.reviewEngine.username)
        assertEquals("password", actual.reviewEngine.password)
        assertEquals("webhookUrl", actual.notifierEngine.wehookUrl)
        assertEquals("channel", actual.notifierEngine.channel)

        val removeCommand = ReviewWorldReception.Command.RemoveCollector("unique_id")
        reception.tell(removeCommand, ActorRef.noSender())

        val actualRemoved = testProbe.expectMsgClass(ReviewWorldReception.Event.CollectorRemoved::class.java)
        assertEquals("unique_id", actualRemoved.collectorId)
    }

    @Test
    fun `receive a remove collector command with the no exist id should raise no event`() {
        reception.tell(
                ReviewWorldReception.Command.RemoveCollector("not_exist_id"),
                ActorRef.noSender())

        testProbe.expectNoMessage()
    }

    @Test
    fun `receive a duplicated add command should ignore the duplicated command`() {
        reception.tell(addCommand, ActorRef.noSender())
        reception.tell(addCommand, ActorRef.noSender())

        testProbe.expectMsgClass(ReviewWorldReception.Event.CollectorAdded::class.java)
        testProbe.expectNoMessage()
    }

    @Test
    fun `receive a get collectors command should return collector status`() {
        reception.tell(addCommand, ActorRef.noSender())

        val actualFuture = Patterns.ask(
                reception,
                ReviewWorldReception.Command.GetCollectors,
                1000)
        val actual = Await.result(actualFuture, Timeout(1, TimeUnit.SECONDS).duration())

        val info = listOf(
                ReviewCollectorInfo("unique_id", ReviewCollectorInfo.State.READY)
        )
        assertEquals(info, actual)
    }
}