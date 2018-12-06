package com.github.lubang.review.world.domain.models.streamline

interface StreamlineRepository {

    fun exist(streamlineId: String): Boolean

    fun create(streamlineId: String): Streamline

    fun get(streamlineId: String): Streamline

    fun delete(streamlineId: String)

}