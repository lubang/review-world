package com.github.lubang.review.world.infra

import akka.actor.AbstractActor
import akka.actor.Props
import com.github.lubang.review.world.core.Review
import com.github.lubang.review.world.domain.reception.Reception
import com.github.lubang.review.world.domain.reception.fetcher.Fetcher
import java.time.ZonedDateTime

class FetcherFixture : Fetcher {
    override fun props(id: String, config: Fetcher.Config): Props {
        return Props.create(FetcherFixture::class.java, id)
    }

    class FetcherFixture(private val id: String) : AbstractActor() {
        override fun createReceive(): Receive {
            return receiveBuilder()
                    .matchEquals(Reception.Command.FetchCommand("failed")) {
                        val response = Reception.Command.FetchResponse(
                                "failed",
                                false,
                                "Connection is disconnected",
                                0)
                        sender.tell(response, self)
                    }
                    .matchEquals(Reception.Command.FetchCommand(id)) {
                        val response = Reception.Command.FetchResponse(
                                it.id,
                                true,
                                "",
                                1)
                        val review = Review(
                                "mock_review_id",
                                "mock_review_id",
                                "mock_project",
                                "mock_branch",
                                "mock_subject",
                                "mock_owner",
                                ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                                ZonedDateTime.parse("2018-10-19T00:00:00Z")
                        )
                        context.system.eventStream()
                                .publish(Fetcher.Event.ReviewFetched(id, review))
                        sender.tell(response, self)
                    }
                    .build()
        }
    }
}