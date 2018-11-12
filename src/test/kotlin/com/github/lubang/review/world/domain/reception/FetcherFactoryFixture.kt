package com.github.lubang.review.world.domain.reception

import akka.actor.AbstractActor
import akka.actor.Props
import com.github.lubang.review.world.domain.reception.fetcher.FetcherConfig
import com.github.lubang.review.world.domain.reception.fetcher.FetcherFactory

class FetcherFactoryFixture : FetcherFactory {
    override fun props(id: String, fetcherConfig: FetcherConfig): Props {
        return Props.create(FetcherFixture::class.java, id)
    }

    class FetcherFixture(val id: String) : AbstractActor() {
        override fun createReceive(): Receive {
            return receiveBuilder()
                    .matchEquals(Reception.Command.FetchCommand("failed")) {
                        val response = Reception.Command.FetchResponse(
                                "failed",
                                false,
                                "Connection is diconnected",
                                0)
                        sender.tell(response, self)
                    }
                    .matchEquals(Reception.Command.FetchCommand(id)) {
                        val response = Reception.Command.FetchResponse(
                                it.id,
                                true,
                                "",
                                7)
                        sender.tell(response, self)
                    }
                    .build()
        }
    }
}