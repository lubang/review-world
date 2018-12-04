package com.github.lubang.review.world.port.adapters.external.servies

import akka.Done
import akka.actor.AbstractActor
import akka.actor.Props
import awaitStringResponse
import com.github.kittinunf.fuel.httpPost
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.domain.common.Review
import kotlinx.coroutines.runBlocking

class SlackNotifierActor : AbstractActor() {
    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Notify::class.java) { notify(it.review, it.config) }
                .build()
    }

    private fun notify(review: Review, config: Config) {
        val url = config.webhookUrl

        val err = runBlocking {
            val (_, _, result) = url.httpPost()
                    .jsonBody(convertNotifyJson(review))
                    .awaitStringResponse()
            result.component2()
        }
        if (err != null) {
            val message = "Illegal response from a notify service" +
                    " (${review.reviewId} | ${err.response.responseMessage})"
            throw IllegalStateException(message)
        }

        sender.tell(Done.done(), self)
    }

    private fun convertNotifyJson(review: Review): String {
        return "{ \"text\": \"${review.subject}\" }"
    }

    companion object {
        fun props(): Props {
            return Props.create(SlackNotifierActor::class.java)
        }
    }

    data class Config(val webhookUrl: String,
                      val channel: String) : NotifierConfig

    data class Notify(val config: Config,
                      val review: Review)
}

