package com.github.lubang.review.world.port.adapters.messaging

import akka.actor.ActorRef
import com.github.lubang.review.world.domain.event.DomainEventSubscriber

class AkkaDomainEventSubscriber(val actor: ActorRef) : DomainEventSubscriber