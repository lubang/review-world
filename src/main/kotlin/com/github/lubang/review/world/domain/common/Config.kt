package com.github.lubang.review.world.domain.common

import java.io.Serializable

interface FetcherConfig : Serializable {
    val fetchInterval: Long
}

interface NotifierConfig : Serializable