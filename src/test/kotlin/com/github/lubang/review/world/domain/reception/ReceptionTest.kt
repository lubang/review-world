package com.github.lubang.review.world.domain.reception

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.domain.reception.status.ReceptionStatus
import com.github.lubang.review.world.infra.gerrit.GerritConfig
import com.github.lubang.review.world.infra.slack.NotifierEngine
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import scala.concurrent.duration.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@DisplayName("A reception")
internal class ReceptionTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe
    private lateinit var mockSender: TestProbe
    private lateinit var reception: ActorRef

    private lateinit var config: ReceptionConfig
    private lateinit var addCommand: Reception.Command.AddCommand
    private lateinit var removeCommand: Reception.Command.RemoveCommand

    @BeforeEach
    fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)
        reception = system.actorOf(Reception.props(FetcherFactoryFixture()))

        config = ReceptionConfig(
                "lubang",
                ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                10000,
                GerritConfig(
                        "https://gerrit.url",
                        "review-world",
                        "lubang",
                        "password"),
                NotifierEngine.Slack("https://webhook.slack.com/19284010", "#notify")
        )
        addCommand = Reception.Command.AddCommand("unique-id", config)
        removeCommand = Reception.Command.RemoveCommand("unique-id")

        mockSender = TestProbe(system)
    }

    @AfterEach
    fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive an add collector command should raise an collector added event and return an add response`() {
        system.eventStream()
                .subscribe(testProbe.ref(), Reception.Event::class.java)

        testProbe.send(reception, addCommand)

        testProbe.expectMsg(Reception.Event.CollectorAdded("unique-id", config))
        testProbe.expectMsg(Reception.Command.AddResponse("unique-id", config, true, ""))
    }

    @Test
    fun `receive an add collector command should update a state added it`() {
        reception.tell(addCommand, mockSender.ref())

        testProbe.send(reception, Reception.Command.GetCollectors)

        testProbe.expectMsg(Reception.Command.GetCollectorsResponse(
                listOf(ReceptionStatus(
                        "unique-id",
                        config,
                        ReceptionStatus.Mode.READY
                ))
        ))
    }

    @Test
    fun `receive a remove collector should raise an collector added event and return a remove response`() {
        system.eventStream()
                .subscribe(testProbe.ref(), Reception.Event::class.java)

        testProbe.send(reception, removeCommand)

        testProbe.expectMsg(Reception.Event.CollectorRemoved("unique-id"))
        testProbe.expectMsg(Reception.Command.RemoveResponse("unique-id"))
    }

    @Test
    fun `receive a remove collector should update a state removed it`() {
        reception.tell(addCommand, mockSender.ref())
        testProbe.send(reception, Reception.Command.GetCollectors)
        testProbe.expectMsg(Reception.Command.GetCollectorsResponse(
                listOf(ReceptionStatus(
                        "unique-id",
                        config,
                        ReceptionStatus.Mode.READY
                ))
        ))

        reception.tell(removeCommand, mockSender.ref())
        testProbe.send(reception, Reception.Command.GetCollectors)
        testProbe.expectMsg(Reception.Command.GetCollectorsResponse(listOf()))
    }

    @Test
    fun `receive a fetch command should return a fetch response`() {
        reception.tell(addCommand, mockSender.ref())

        testProbe.send(reception, Reception.Command.FetchCommand(addCommand.id))

        testProbe.expectMsg(
                Duration.create(10, TimeUnit.SECONDS),
                Reception.Command.FetchResponse(addCommand.id, true, "", 7))
    }

    @Test
    fun `receive a fetch command with non-existent id should return a failed response`() {
        testProbe.send(reception, Reception.Command.FetchCommand("nonexistent-id"))

        testProbe.expectMsg(
                Duration.create(10, TimeUnit.SECONDS),
                Reception.Command.FetchResponse(
                        "nonexistent-id",
                        false,
                        "Fetch ID `nonexistent-id` is not exist in a reception",
                        0))
    }
}