package com.github.lubang.review.world.infra.slack

import com.github.lubang.review.world.domain.reception.notifier.Notifier

data class SlackConfig(val webhookUrl: String,
                       val channel: String) : Notifier.Config
