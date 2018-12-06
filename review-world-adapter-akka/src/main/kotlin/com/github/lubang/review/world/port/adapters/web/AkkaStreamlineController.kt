package com.github.lubang.review.world.port.adapters.web

import akka.http.javadsl.server.Directives.*
import akka.http.javadsl.server.Route
import akka.http.javadsl.unmarshalling.Unmarshaller
import com.github.lubang.review.world.application.command.CreateStreamlineCommand
import com.github.lubang.review.world.application.command.FetchStreamlineCommand
import com.github.lubang.review.world.application.command.StreamlineCommandHandler
import com.github.lubang.review.world.application.query.StreamlineInfo
import com.github.lubang.review.world.application.query.StreamlineQueryModel
import com.github.lubang.review.world.domain.common.FetcherConfig
import com.github.lubang.review.world.domain.common.NotifierConfig
import com.github.lubang.review.world.domain.models.streamline.StreamlineRepository
import com.github.lubang.review.world.port.adapters.serialization.GsonSupport
import java.time.ZonedDateTime

class AkkaStreamlineController(repository: StreamlineRepository) {

    private val commandHandler = StreamlineCommandHandler(repository)
    private val queryModel = StreamlineQueryModel(repository)

    fun getStreamlines(): Route? {
        return complete("[]")
    }

    fun createStreamline(streamlineId: String): Route? {
        return entity(Unmarshaller.entityToString()) { content ->
            val request = GsonSupport.gson.fromJson(
                    content,
                    CreateStreamlineRequest::class.java)

            val command = CreateStreamlineCommand(streamlineId,
                    request.register,
                    ZonedDateTime.now(),
                    request.fetcherConfig,
                    request.notifiersConfigs)
            commandHandler.execute(command)

            getStreamlineInfo(streamlineId)
        }
    }

    fun fetch(streamlineId: String): Route? {
        val command = FetchStreamlineCommand(streamlineId)
        commandHandler.execute(command)
        return getStreamlineInfo(streamlineId)
    }

    fun getStreamlineInfo(streamlineId: String): Route? {
        val streamlineStatus = queryModel.getStreamlineStatus(streamlineId)
        val response = streamlineStatus.thenApply {
            GsonSupport.gson.toJson(CreateStreamlineResponse(it))
        }
        return completeOKWithFutureString(response)
    }

    data class CreateStreamlineRequest(val register: String,
                                       val fetcherConfig: FetcherConfig,
                                       val notifiersConfigs: Set<NotifierConfig>)

    data class CreateStreamlineResponse(val info: StreamlineInfo)
}