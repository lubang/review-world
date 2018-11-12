package com.github.lubang.review.world.infra.gerrit

import com.github.lubang.review.world.domain.reception.fetcher.FetcherConfig

data class GerritConfig(val url: String,
                        val project: String,
                        val username: String,
                        val password: String) : FetcherConfig {
}
