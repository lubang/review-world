package com.github.lubang.review.world.reception

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.pattern.Patterns
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import akka.util.Timeout
import com.github.lubang.review.world.review.ReviewEngine
import com.github.lubang.review.world.notifier.NotifierEngine
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import scala.concurrent.Await
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@DisplayName("A review world reception")
class ReviewWorldReceptionTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe
    private lateinit var reception: ActorRef
    private lateinit var addCommand: ReviewWorldReception.Command.AddCollector
    private lateinit var expectedCollectorInfo: ReviewCollectorInfo

    private val timeout = Timeout(1, TimeUnit.SECONDS).duration()

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

        expectedCollectorInfo = ReviewCollectorInfo("unique_id",
                ReviewCollectorInfo.Status.READY,
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

        testProbe.expectNoMessage(Timeout(1, TimeUnit.SECONDS).duration())
    }

    @Test
    fun `receive duplicated add commands should ignore the second`() {
        reception.tell(addCommand, ActorRef.noSender())
        reception.tell(addCommand, ActorRef.noSender())

        testProbe.expectMsgClass(ReviewWorldReception.Event.CollectorAdded::class.java)
        testProbe.expectNoMessage(timeout)
    }

    @Test
    fun `receive a get collectors command should return a collector status`() {
        reception.tell(addCommand, ActorRef.noSender())

        val actualFuture = Patterns.ask(reception, ReviewWorldReception.Command.GetCollectors, 1000)
        val actualList = Await.result(actualFuture, timeout) as List<*>
        val actual = actualList[0] as ReviewCollectorInfo

        assertEquals(expectedCollectorInfo.collectorId, actual.collectorId)
        assertEquals(expectedCollectorInfo.status, actual.status)
        assertEquals(expectedCollectorInfo.register, actual.register)
        assertEquals(expectedCollectorInfo.registeredAt, actual.registeredAt)
        assertEquals(expectedCollectorInfo.reviewEngine, actual.reviewEngine)
        assertEquals(expectedCollectorInfo.notifierEngine, actual.notifierEngine)
    }

    @Test
    fun `receive a start collector command should update a collector status with running state`() {
        reception.tell(addCommand, ActorRef.noSender())

        reception.tell(ReviewWorldReception.Command.StartCollector(addCommand.collectorId), ActorRef.noSender())
        val actualFuture = Patterns.ask(reception, ReviewWorldReception.Command.GetCollectors, 1000)
        val actual = Await.result(actualFuture, timeout)

        expectedCollectorInfo.status = ReviewCollectorInfo.Status.RUNNING
        assertEquals(listOf(expectedCollectorInfo), actual)
    }

    @Test
    fun `receive duplicated start collectors command should ignore the second`() {
        reception.tell(addCommand, ActorRef.noSender())

        reception.tell(ReviewWorldReception.Command.StartCollector(addCommand.collectorId), ActorRef.noSender())
        reception.tell(ReviewWorldReception.Command.StartCollector(addCommand.collectorId), ActorRef.noSender())
        val actualFuture = Patterns.ask(reception, ReviewWorldReception.Command.GetCollectors, 1000)
        val actual = Await.result(actualFuture, timeout)

        expectedCollectorInfo.status = ReviewCollectorInfo.Status.RUNNING
        assertEquals(listOf(expectedCollectorInfo), actual)
    }

    @Test
    fun `receive a start collector command should ignore a non-existent collector by ID`() {
        reception.tell(addCommand, ActorRef.noSender())

        reception.tell(ReviewWorldReception.Command.StartCollector("no-id"), ActorRef.noSender())
        val actualFuture = Patterns.ask(reception, ReviewWorldReception.Command.GetCollectors, 1000)
        val actual = Await.result(actualFuture, timeout)

        assertEquals(listOf(expectedCollectorInfo), actual)
    }

    @Test
    fun `receive a shutdown command and restart should restore the status`() {
        reception.tell(addCommand, ActorRef.noSender())

        reception.tell(ReviewWorldReception.Command.Shutdown, ActorRef.noSender())

        Thread.sleep(100)

        reception = system.actorOf(ReviewWorldReception.props())
        val actualFuture = Patterns.ask(reception, ReviewWorldReception.Command.GetCollectors, 1000)
        val actual = Await.result(actualFuture, timeout)

        assertEquals(listOf(expectedCollectorInfo), actual)
    }
}