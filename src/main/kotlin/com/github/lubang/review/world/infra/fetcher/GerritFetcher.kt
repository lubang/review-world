package com.github.lubang.review.world.infra.fetcher

import akka.actor.AbstractActor
import akka.actor.Props
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.Review
import com.google.gson.Gson
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class GerritFetcher : AbstractActor() {

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Command.FetchGerrit::class.java) { fetch(it.streamlineId, it.config) }
                .build()
    }

    private fun fetch(streamlineId: String, config: Config) {
        val originSender = sender

        val url = "${config.url}/r/a/changes/?q=project:${config.project}+statuses:open"
        url.httpGet()
                .authenticate(config.username, config.password)
                .responseObject(Deserializer()) { _, _, result ->
                    val (changes, err) = result
                    if (err == null) {
                        val reviews = changes
                                ?.map { parseToReviews(streamlineId, it) }
                                ?.toSet()
                        originSender.tell(reviews, self)
                    }
                }
    }

    private fun parseToReviews(streamlineId: String, change: Change): Review {
        return Review(
                streamlineId,
                "GERRIT+${change.id}",
                change.project,
                change.branch,
                change.subject,
                change.owner._account_id,
                change.getCreatedAt(),
                change.getUpdatedAt()
        )
    }

    companion object {
        fun props(): Props {
            return Props.create(GerritFetcher::class.java)
        }

        private const val BODY_START_SYNTAX = ")]}'"

        private val dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss.n")
                .withZone(ZoneOffset.UTC)
    }

    data class Config(override val fetchInterval: Long,
                      val url: String,
                      val project: String,
                      val username: String,
                      val password: String) : FetcherConfig

    interface Command {
        data class FetchGerrit(val streamlineId: String,
                               val config: Config)
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
            return ZonedDateTime.parse(created, dateTimeFormatter)
        }

        fun getUpdatedAt(): ZonedDateTime {
            return ZonedDateTime.parse(updated, dateTimeFormatter)
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