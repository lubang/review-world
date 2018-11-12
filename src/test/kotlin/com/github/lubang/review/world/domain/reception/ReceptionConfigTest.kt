package com.github.lubang.review.world.domain.reception

import com.github.lubang.review.world.infra.gerrit.GerritConfig
import com.github.lubang.review.world.infra.slack.SlackConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

@DisplayName("A reception config")
class ReceptionConfigTest {

    @Test
    fun `validate a fetchInterval is larger than 10 secs should raise an IllegalArgumentException`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            ReceptionConfig(
                    "lubang",
                    ZonedDateTime.parse("2018-10-19T00:00:00Z"),
                    1000,
                    GerritConfig(
                            "https://gerrit.url",
                            "review-world",
                            "lubang",
                            "password"),
                    SlackConfig("https://webhook.slack.com/19284010", "#notify"))
        }
        assertEquals(
                "ReceptionConfig `fetchInterval` should be larger than 10000 millis",
                exception.message)
    }

}