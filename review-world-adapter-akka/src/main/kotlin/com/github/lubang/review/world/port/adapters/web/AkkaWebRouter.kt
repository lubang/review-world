package com.github.lubang.review.world.port.adapters.web

import akka.http.javadsl.server.HttpApp
import akka.http.javadsl.server.PathMatchers.remainingPath
import akka.http.javadsl.server.Route
import com.github.lubang.review.world.domain.models.streamline.StreamlineRepository


class AkkaWebRouter(repository: StreamlineRepository) : HttpApp() {

    private val streamlineController = AkkaStreamlineController(repository)

    public override fun routes(): Route {
        return pathPrefix("api") {
            pathPrefix("streamlines") {
                pathEnd {
                    get {
                        streamlineController.getStreamlines()
                    }
                }.orElse(pathSuffixTest("fetch") {
                    path(remainingPath()) {
                        streamlineController.fetch(it.head().toString())
                    }
                }).orElse(path {
                    get {
                        streamlineController.getStreamlineInfo(it)
                    }.orElse(post {
                        streamlineController.createStreamline(it)
                    })
                })
            }.orElse(pathPrefix("reviews") {
                pathEnd {
                    get {
                        complete("[]")
                    }
                }
            })
        }.orElse(pathSingleSlash {
            getFromResource("web/index.html")
        }.orElse(path(remainingPath()) {
            getFromResource("web/$it")
        }))
    }
}