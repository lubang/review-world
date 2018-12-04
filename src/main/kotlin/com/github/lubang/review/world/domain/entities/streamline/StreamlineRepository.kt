package com.github.lubang.review.world.domain.entities.streamline

interface StreamlineRepository {

    fun existById(streamlineId: String): Boolean

    fun create(streamlineId: String): Streamline

    fun getById(streamlineId: String): Streamline

    fun delete(streamlineId: String)

}