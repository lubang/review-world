package com.github.lubang.review.world.infra.gerrit

import akka.actor.AbstractActor
import akka.actor.ActorRef
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.lubang.review.world.core.Review
import com.github.lubang.review.world.domain.reception.Reception
import com.github.lubang.review.world.domain.reception.fetcher.Fetcher
import com.google.gson.Gson
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class GerritFetcher(
        private val id: String,
        private val fetcher: GerritConfig)
    : AbstractActor() {

    override fun createReceive(): Receive {
        return receiveBuilder()
                .matchEquals(Reception.Command.FetchCommand(id)) { fetch() }
                .build()
    }

    private fun fetch() {
        val originSender = sender
        val url = "${fetcher.url}/r/a/changes/?q=project:${fetcher.project}+status:open"
        url.httpGet()
                .authenticate(fetcher.username, fetcher.password)
                .responseObject(Deserializer()) { _, _, result ->
                    val (changes, err) = result

                    if (err != null) {
                        responseFetchResult(
                                originSender,
                                false,
                                err.message ?: "",
                                changes?.size ?: 0)
                    } else {
                        val reviews = changes
                                ?.map { parseToReviews(it) }
                                ?.toList()
                        sendToReviewRouter(reviews)
                        responseFetchResult(
                                originSender,
                                true,
                                "",
                                reviews?.size ?: 0)
                    }
                }
    }

    private fun parseToReviews(change: Change): Review {
        return Review(
                id,
                "RWID+${change.id}",
                change.project,
                change.branch,
                change.subject,
                change.owner._account_id,
                change.getCreatedAt(),
                change.getUpdatedAt()
        )
    }

    private fun sendToReviewRouter(reviews: List<Review>?) {
        reviews?.forEach {
            context.system.eventStream()
                    .publish(Fetcher.Event.ReviewFetched(id, it))
        }
    }

    private fun responseFetchResult(sender: ActorRef, isSuccess: Boolean, reason: String, size: Int) {
        val response = Reception.Command.FetchResponse(id, isSuccess, reason, size)
        sender.tell(response, self)
    }


    companion object {
        val DATE_TIME_FORMATTER = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss.n")
                .withZone(ZoneOffset.UTC)!!

        const val BODY_START_SYNTAX = ")]}'"
    }

    data class Change(
            val id: String,
            val project: String,
            val branch: String,
            val hashtags: List<String>,
            val change_id: String,
            val subject: String,
            val status: String,
            private val created: String,
            private val updated: String,
            val submit_type: String,
            val mergeable: String,
            val insertions: String,
            val deletions: String,
            val unresolved_comment_count: Int,
            val _number: String,
            val owner: Owner) {

        fun getCreatedAt(): ZonedDateTime {
            return ZonedDateTime.parse(created, DATE_TIME_FORMATTER)
        }

        fun getUpdatedAt(): ZonedDateTime {
            return ZonedDateTime.parse(updated, DATE_TIME_FORMATTER)
        }

        data class Owner(val _account_id: String)
    }

    class Changes : ArrayList<Change>()

    class Deserializer : ResponseDeserializable<Changes> {
        override fun deserialize(content: String): Changes {
            val json = content.removePrefix(BODY_START_SYNTAX)
            return Gson().fromJson(json, Changes::class.java)
        }
    }
}