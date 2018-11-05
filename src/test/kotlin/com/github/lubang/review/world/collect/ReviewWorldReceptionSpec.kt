package com.github.lubang.review.world.collect

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import com.github.lubang.review.world.notifier.NotifierEngine
import com.github.lubang.review.world.reception.ReviewWorldReception
import com.github.lubang.review.world.review.ReviewEngine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.spekframework.spek2.Spek
import java.time.ZonedDateTime

object ReviewWorldReceptionSpec : Spek({
    group("A review world reception") {
        val system = ActorSystem.create()
        val testProbe = TestProbe(system)
        system.eventStream().subscribe(
                testProbe.ref(),
                ReviewWorldReception.Event::class.java)

        val reception = system.actorOf(ReviewWorldReception.props())

        test("receive an add/remove collector command should raise events") {
            reception.tell(ReviewWorldReception.Command.AddCollector(
                    "unique_id",
                    "register_name",
                    ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                    ReviewEngine.Gerrit(
                            "gerrit_url",
                            "project",
                            "username",
                            "password"
                    ),
                    NotifierEngine.Slack("webhookUrl", "channel")
            ), ActorRef.noSender())

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

            reception.tell(ReviewWorldReception.Command.RemoveCollector("unique_id"), ActorRef.noSender())

            val actualRemoved = testProbe.expectMsgClass(ReviewWorldReception.Event.CollectorRemoved::class.java)
            assertEquals("unique_id", actualRemoved.collectorId)
        }

        test("receive a remove collector command with the no exist id should raise no event") {
            reception.tell(
                    ReviewWorldReception.Command.RemoveCollector("not_exist_id"),
                    ActorRef.noSender())

            testProbe.expectNoMessage()
        }

        test("receive a duplicated add command should ignore the duplicated command") {
            val cmd = ReviewWorldReception.Command.AddCollector(
                    "unique_id",
                    "register_name",
                    ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                    ReviewEngine.Gerrit(
                            "gerrit_url",
                            "project",
                            "username",
                            "password"
                    ),
                    NotifierEngine.Slack("webhookUrl", "channel")
            )
            reception.tell(cmd, ActorRef.noSender())
            reception.tell(cmd, ActorRef.noSender())

            testProbe.expectMsgClass(ReviewWorldReception.Event.CollectorAdded::class.java)
            testProbe.expectNoMessage()
        }
    }
})