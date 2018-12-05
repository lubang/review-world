package com.github.lubang.review.world.application.command

import com.github.lubang.review.world.domain.models.streamline.StreamlineRepository

class StreamlineCommandHandler(private val repository: StreamlineRepository) {

    fun execute(command: CreateStreamlineCommand) {
        if (repository.exist(command.streamlineId)) {
            throw IllegalArgumentException("Streamline `${command.streamlineId}` is already created")
        }

        val streamline = repository.create(command.streamlineId)
        streamline.createStreamline(
                command.register,
                command.fetcherConfig,
                command.notifiersConfigs)
    }

    fun execute(command: FetchStreamlineCommand) {
        val streamline = repository.get(command.streamlineId)
        streamline.fetch()
    }

}