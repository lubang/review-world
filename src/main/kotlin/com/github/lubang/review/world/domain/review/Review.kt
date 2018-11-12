package com.github.lubang.review.world.domain.review

import java.time.ZonedDateTime

data class Review(val id: String,
                  val reviewId: String,
                  val project: String,
                  val branch: String,
                  val subject: String,
                  val owner: String,
                  val createdAt: ZonedDateTime,
                  val updatedAt: ZonedDateTime
)