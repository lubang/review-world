package com.github.lubang.review.world.reception

class ReviewWorldReceptionState {

    private val collectors: MutableMap<String, ReviewCollectorInfo> = mutableMapOf()

    fun update(event: ReviewWorldReception.Event) {
        when (event) {
            is ReviewWorldReception.Event.CollectorAdded -> {
                val reviewCollectorInfo = ReviewCollectorInfo(
                        event.collectorId,
                        ReviewCollectorInfo.State.READY)
                collectors[event.collectorId] = reviewCollectorInfo
            }
            is ReviewWorldReception.Event.CollectorRemoved -> {
                collectors.remove(event.collectorId)
            }
        }
    }

    fun hasCollector(collectorId: String): Boolean {
        return collectors.contains(collectorId)
    }

    fun getCollectors(): List<ReviewCollectorInfo> {
        return collectors.values.toList()
    }
}