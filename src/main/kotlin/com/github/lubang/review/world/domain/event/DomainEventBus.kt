package com.github.lubang.review.world.domain.event

interface DomainEventBus {

    fun publish(event: DomainEvent)

    fun subscribe(subscriber: DomainEventSubscriber, channel: Class<*>)

    fun unsubscribe(subscriber: DomainEventSubscriber)

}