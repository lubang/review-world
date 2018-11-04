package com.github.lubang.review.world.notifier

import java.io.Serializable

open class NotifierEngine : Serializable {

    class Slack(val wehookUrl: String,
                val channel: String) : NotifierEngine()

}
