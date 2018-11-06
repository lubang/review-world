package com.github.lubang.review.world.review

sealed class ReviewEngine {

    data class Gerrit(val url: String,
                      val project: String,
                      val username: String,
                      val password: String) : ReviewEngine()

}
