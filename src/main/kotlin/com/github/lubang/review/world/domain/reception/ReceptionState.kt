package com.github.lubang.review.world.domain.reception

import com.github.lubang.review.world.domain.reception.status.ReceptionStatus

class ReceptionState {
    private val status: MutableMap<String, ReceptionStatus> = mutableMapOf()

    fun update(event: Reception.Event) {
        when (event) {
            is Reception.Event.CollectorAdded -> {
                status[event.id] = ReceptionStatus(
                        event.id,
                        event.config,
                        ReceptionStatus.Mode.READY)
            }
            is Reception.Event.CollectorRemoved -> {
                status.remove(event.id)
            }
        }
    }

    fun getStatus(): List<ReceptionStatus> {
        return status.values.toList()
    }

    fun hasRequest(id: String): Boolean {
        return status.containsKey(id)
    }

    fun getRequest(id: String): ReceptionConfig {
        return status[id]!!.config
    }
}
