package com.github.lubang.review.world

import com.google.inject.Guice

fun main(args: Array<String>) {
    val injector = Guice.createInjector(ReviewWorldModule())
    val server = injector.getInstance(ReviewWorldApplication::class.java)
    server.start()
}