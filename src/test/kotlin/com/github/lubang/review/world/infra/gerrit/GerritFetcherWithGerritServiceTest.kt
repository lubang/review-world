package com.github.lubang.review.world.infra.gerrit

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestProbe
import akka.testkit.javadsl.TestKit
import com.github.lubang.review.world.domain.reception.Reception
import com.github.lubang.review.world.domain.reception.fetcher.Fetcher
import org.junit.jupiter.api.*

@DisplayName("A Gerrit fetcher (with Gerrit service)")
internal class GerritFetcherWithGerritServiceTest {

    private lateinit var system: ActorSystem
    private lateinit var testProbe: TestProbe
    private lateinit var gerritFetcher: ActorRef
    private lateinit var gerritConfig: GerritConfig

    @BeforeEach
    fun setup() {
        system = ActorSystem.create()
        testProbe = TestProbe(system)

        // Change a real gerrit service configurations
        gerritConfig = GerritConfig(
                "https://gerrit.url",
                "review-world",
                "lubang",
                "password")
        gerritFetcher = system.actorOf(Props.create(GerritFetcher::class.java, "id", gerritConfig))
    }

    @AfterEach
    fun teardown() {
        TestKit.shutdownActorSystem(system)
    }

    @Disabled
    @Test
    fun `receive a fetch command should raise events`() {
        system.eventStream()
                .subscribe(testProbe.ref(), Fetcher.Event::class.java)

        testProbe.send(gerritFetcher, Reception.Command.FetchCommand("id"))

        testProbe.expectMsgClass(Fetcher.Event::class.java)
    }
}