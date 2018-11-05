package com.github.lubang.review.world.collector

import java.io.Serializable

open class ReviewEngine : Serializable {

    class Gerrit(val url: String,
                 val project: String,
                 val username: String,
                 val password: String) : ReviewEngine()

}
