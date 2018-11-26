package com.github.lubang.review.world

import java.io.File

object TestPropertyHelper {
    private val env: Map<String, String> = File("properties.test.env").readLines().map {
        val (key, value) = it.split('=')
        Pair(key, value)
    }.toMap()

    val githubUsername
        get() = getFileOrEnv("GITHUB_USERNAME")

    val githubPassword
        get() = getFileOrEnv("GITHUB_PASSWORD")

    val gerritUrl
        get() = getFileOrEnv("GERRIT_URL")

    val gerritProject
        get() = getFileOrEnv("GERRIT_PROJECT")

    val gerritUsername
        get() = getFileOrEnv("GERRIT_USERNAME")

    val gerritPassword
        get() = getFileOrEnv("GERRIT_PASSWORD")

    val slackWebhook
        get() = getFileOrEnv("SLACK_WEBHOOK")

    val slackChannel
        get() = getFileOrEnv("SLACK_CHANNEL")


    private fun getFileOrEnv(key: String): String {
        return if (env[key] == null) {
            System.getenv(key)!!
        } else {
            env[key]!!
        }
    }
}