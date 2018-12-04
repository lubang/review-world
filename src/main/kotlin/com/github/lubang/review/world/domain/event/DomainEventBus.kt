package com.github.lubang.review.world.domain.event

interface DomainEventBus {

    fun publish(event: DomainEvent)

    fun subscribe(subscriber: DomainEventSubscriber, channel: Class<DomainEvent>)

    fun unsubscribe(subscriber: DomainEventSubscriber)

}