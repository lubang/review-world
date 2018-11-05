package com.github.lubang.review.world.reception

data class ReviewCollectorInfo(val collectorId: String,
                               var state: State) {
    enum class State {
        NONE, READY, RUNNING,
    }
}