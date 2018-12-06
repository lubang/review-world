package com.github.lubang.review.world.port.adapters.actor

import akka.actor.ActorSystem

object AkkaSupport {
    lateinit var system: ActorSystem
        private set

    fun initialize(system: ActorSystem) {
        this.system = system
    }
}