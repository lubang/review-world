package com.github.lubang.review.world.port.adapters

import akka.actor.ActorSystem
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.stream.ActorMaterializer
import com.github.lubang.review.world.ReviewWorldApplication
import com.github.lubang.review.world.domain.models.streamline.StreamlineRepository
import com.github.lubang.review.world.port.adapters.actor.AkkaSupport
import com.github.lubang.review.world.port.adapters.actor.models.AkkaStreamlineSchedulerActor
import com.github.lubang.review.world.port.adapters.web.AkkaWebRouter
import javax.inject.Inject

class AkkaReviewWorldApplication
@Inject constructor(private val streamlineRepository: StreamlineRepository)
    : ReviewWorldApplication {

    private val host = "localhost"
    private val port = 8090

    override fun start() {
        val system = AkkaSupport.system

        startScheduler(system)
        startWebServer(system)
    }

    private fun startScheduler(system: ActorSystem) {
        system.actorOf(AkkaStreamlineSchedulerActor.props(streamlineRepository))
    }

    private fun startWebServer(system: ActorSystem) {
        val materializer = ActorMaterializer.create(system)

        val http = Http.get(system)
        val webRouter = AkkaWebRouter(streamlineRepository)
        val routeFlow = webRouter.routes().flow(system, materializer)

        println("Server up $host:$port")

        val httpBinder = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host, port),
                materializer)

        system.registerOnTermination {

            println("Server down $host:$port")

            httpBinder
                    .thenCompose { it.unbind() }
                    .thenAccept { system.terminate() }
        }
    }

}