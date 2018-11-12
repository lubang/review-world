package com.github.lubang.review.world.infra.slack

sealed class NotifierEngine {

    data class Slack(val webhookUrl: String,
                     val channel: String) : NotifierEngine()

}
