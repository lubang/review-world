package com.github.lubang.review.world.infra

import com.github.lubang.review.world.domain.reception.fetcher.Fetcher
import com.github.lubang.review.world.infra.gerrit.GerritConfig
import com.github.lubang.review.world.infra.gerrit.GerritFetcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ReceptionFetcherTest {

    private lateinit var fetcher: Fetcher

    @BeforeEach
    private fun setup() {
        fetcher = ReceptionFetcher()
    }

    @Test
    fun `props with a gerrit fetcher config should create a gerrit fetcher`() {
        val fetcherConfig = GerritConfig(
                "https://gerrit.url",
                "review-world",
                "lubang",
                "password")

        val actual = fetcher.props("hi", fetcherConfig)

        assertEquals(GerritFetcher::class.java, actual.clazz())
    }

    @Test
    fun `props with an invalid fetcher config should throw IllegalArgumentException`() {
        class InvalidConfig : Fetcher.Config

        val fetcherConfig = InvalidConfig()
        val id = "fetcher_id"

        val exception = assertThrows<IllegalArgumentException> {
            fetcher.props(id, fetcherConfig)
        }

        assertEquals("Reception ID `$id` should not have a valid config", exception.message)
    }

}