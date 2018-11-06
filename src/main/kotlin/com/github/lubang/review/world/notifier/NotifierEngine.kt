package com.github.lubang.review.world.notifier

sealed class NotifierEngine {

    data class Slack(val wehookUrl: String,
                     val channel: String) : NotifierEngine()

}
