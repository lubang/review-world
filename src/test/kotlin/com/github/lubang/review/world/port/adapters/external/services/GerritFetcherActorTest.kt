package com.github.lubang.review.world.port.adapters.external.services

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.TestPropertyHelper
import com.github.lubang.review.world.port.adapters.actor.models.AkkaStreamlineActor
import com.github.lubang.review.world.port.adapters.external.servies.GerritFetcherActor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Gerrit fetcher")
internal class GerritFetcherActorTest {
    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe

    private lateinit var fetcher: ActorRef

    @BeforeEach
    private fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)

        fetcher = system.actorOf(GerritFetcherActor.props())
    }

    @AfterEach
    private fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Test
    fun `receive a Fetch should return a reviews`() {
        val command = GerritFetcherActor.Fetch(
                "streamline_id",
                GerritFetcherActor.Config(
                        10000,
                        TestPropertyHelper.gerritUrl,
                        TestPropertyHelper.gerritProject,
                        TestPropertyHelper.gerritUsername,
                        TestPropertyHelper.gerritPassword
                ))
        testProbe.send(fetcher, command)

        testProbe.expectMsgClass(AkkaStreamlineActor.Notify::class.java)
    }
}