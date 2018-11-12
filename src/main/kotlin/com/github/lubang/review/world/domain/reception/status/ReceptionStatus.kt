package com.github.lubang.review.world.domain.reception.status

import com.github.lubang.review.world.domain.reception.ReceptionConfig

data class ReceptionStatus(val id: String,
                           val config: ReceptionConfig,
                           val mode: Mode) {

    enum class Mode {
        READY, RUNNING,
    }

}