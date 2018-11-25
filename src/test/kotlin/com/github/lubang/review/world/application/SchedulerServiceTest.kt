package com.github.lubang.review.world.application

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.domain.streamline.Streamline
import com.github.lubang.review.world.infra.fetcher.GerritFetcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("A scheduler service")
internal class SchedulerServiceTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe

    private lateinit var schedulerService: ActorRef

    @BeforeEach
    private fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)

        schedulerService = system.actorOf(SchedulerService.props())
        // wait for registering the scheduler service subscriber in an event stream
        Thread.sleep(100)
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `subscribe a Streamline_Event_Started should register the scheduler`() {
        val event = Streamline.Event.Started(
                "streamlineId",
                GerritFetcher.Config(
                        10000,
                        "https://gerrit.url",
                        "review-world",
                        "lubang",
                        "password"))
        system.eventStream().publish(event)

        testProbe.send(schedulerService, SchedulerService.Command.GetSchedules)
        testProbe.expectMsg(setOf("streamlineId"))
    }

    @Test
    fun `subscribe a Streamline_Event_Stopped should unregister the scheduler`() {
        `subscribe a Streamline_Event_Started should register the scheduler`()

        val event = Streamline.Event.Stopped("streamlineId")
        system.eventStream().publish(event)

        testProbe.send(schedulerService, SchedulerService.Command.GetSchedules)
        testProbe.expectMsg(emptySet<String>())
    }
}