package com.github.lubang.review.world.infra.fetcher

import akka.actor.AbstractActor
import akka.actor.Props
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpPost
import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.Review
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.time.ZonedDateTime


class GithubFetcher : AbstractActor() {
    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(Command.FetchGithub::class.java) { fetch(it.streamlineId, it.config) }
                .build()
    }

    private fun fetch(streamlineId: String, config: Config) {
        val originSender = sender

        val url = config.githubGraphQlUrl
        url.httpPost()
                .authenticate(config.username, config.password)
                .jsonBody("{\"query\":\"{\\n" +
                        "  repository(owner: \\\"${config.owner}\\\", name: \\\"${config.repository}\\\") {\\n" +
                        "    pullRequests(last: 100, states: OPEN) {\\n" +
                        "      totalCount\\n" +
                        "      edges {\\n" +
                        "        node {\\n" +
                        "          number\\n" +
                        "          title\\n" +
                        "          author {\\n" +
                        "            login\\n" +
                        "          }\\n" +
                        "          createdAt\\n" +
                        "          updatedAt\\n" +
                        "          url\\n" +
                        "          headRefName\\n" +
                        "        }\\n" +
                        "      }\\n" +
                        "    }\\n" +
                        "  }\\" +
                        "n}\\" +
                        "n\"}")
                .responseObject(Deserializer()) { _, _, result ->
                    val (pullRequests, err) = result
                    if (err == null) {
                        val reviews = pullRequests
                                ?.map { parseToReviews(streamlineId, config.repository, it) }
                                ?.toSet()
                        originSender.tell(reviews, self)
                    }
                }
    }

    private fun parseToReviews(streamlineId: String, project: String, pullRequest: PullRequest): Review {
        return Review(
                streamlineId,
                "GITHUB-PR+${pullRequest.number}",
                project,
                pullRequest.headRefName,
                pullRequest.title,
                pullRequest.author.login,
                pullRequest.toReviewCreatedAt(),
                pullRequest.toReviewUpdatedAt()
        )
    }

    companion object {
        fun props(): Props {
            return Props.create(GithubFetcher::class.java)
        }
    }

    data class Config(override val fetchInterval: Long,
                      val githubGraphQlUrl: String,
                      val owner: String,
                      val repository: String,
                      val username: String,
                      val password: String) : FetcherConfig

    interface Command {
        data class FetchGithub(val streamlineId: String,
                               val config: Config)
    }

    data class PullRequest(
            val number: Long,
            val title: String,
            val author: Author,
            val createdAt: String,
            val updatedAt: String,
            val headRefName: String) {

        fun toReviewCreatedAt(): ZonedDateTime {
            return ZonedDateTime.parse(createdAt)
        }

        fun toReviewUpdatedAt(): ZonedDateTime {
            return ZonedDateTime.parse(updatedAt)
        }

        data class Author(val login: String)
    }

    class Deserializer : ResponseDeserializable<Set<PullRequest>> {
        override fun deserialize(content: String): Set<PullRequest> {
            val gson = Gson()

            val parser = JsonParser()
            val edges = parser.parse(content).asJsonObject
                    .getAsJsonObject("data")
                    .getAsJsonObject("repository")
                    .getAsJsonObject("pullRequests")
                    .getAsJsonArray("edges")
            return edges.map {
                val node = it.asJsonObject.getAsJsonObject("node")
                gson.fromJson(node, PullRequest::class.java)
            }.toSet()
        }
    }
}