package com.subakstudio.sandbox.cafenotifier

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by yeoupooh on 4/20/16.
 */
class CafeNotifierConfigTest {
    @Test
    fun load() {
        val config: CafeNotifierConfig = CafeNotifierConfig()
        config.load()
        assertTrue(config.telegramIntervalMs > 0)
        assertNotNull(config.token)
        assertTrue(config.clubId > 0)
        assertTrue(config.cafeIntervalMs > 0)
    }

}