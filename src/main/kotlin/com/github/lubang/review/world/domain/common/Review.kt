package com.github.lubang.review.world.domain.common

import java.io.Serializable
import java.time.ZonedDateTime

data class Review(val streamlineId: String,
                  val reviewId: String,
                  val project: String,
                  val branch: String,
                  val subject: String,
                  val owner: String,
                  val createdAt: ZonedDateTime,
                  val updatedAt: ZonedDateTime) : Serializable