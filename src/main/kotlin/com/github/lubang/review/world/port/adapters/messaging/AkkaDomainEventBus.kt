package com.github.lubang.review.world.port.adapters.messaging

import com.github.lubang.review.world.domain.event.DomainEvent
import com.github.lubang.review.world.domain.event.DomainEventBus
import com.github.lubang.review.world.domain.event.DomainEventSubscriber
import com.github.lubang.review.world.port.adapters.actor.AkkaSupport

class AkkaDomainEventBus : DomainEventBus {

    override fun publish(event: DomainEvent) {
        AkkaSupport.system.eventStream().publish(event)
    }

    override fun subscribe(subscriber: DomainEventSubscriber,
                           channel: Class<DomainEvent>) {
        if (subscriber !is AkkaDomainEventSubscriber) {
            throw IllegalArgumentException("DomainEventSubscriber should be `AkkaDomainEventSubscriber`")
        }

        AkkaSupport.system.eventStream().subscribe(subscriber.actor, channel)
    }

    override fun unsubscribe(subscriber: DomainEventSubscriber) {
        AkkaSupport.system.eventStream().unsubscribe(subscriber)
    }

}