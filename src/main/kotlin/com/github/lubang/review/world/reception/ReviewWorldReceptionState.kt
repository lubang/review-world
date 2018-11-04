package com.github.lubang.review.world.reception

class ReviewWorldReceptionState {

    private val collectors: MutableList<String> = mutableListOf()

    fun update(event: ReviewWorldReception.Event) {
        when (event) {
            is ReviewWorldReception.Event.CollectorAdded -> collectors.add(event.collectorId)
            is ReviewWorldReception.Event.CollectorRemoved -> collectors.remove(event.collectorId)
        }
    }

    fun hasCollector(collectorId: String): Boolean {
        return collectors.contains(collectorId)
    }
}