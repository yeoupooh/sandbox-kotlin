package com.subakstudio.sandbox.cafenotifier.search

import com.subakstudio.sandbox.cafenotifier.CafeNotifierConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Created by yeoupooh on 16. 4. 19.
 */
class CafeSearchTest {
    @Test
    fun search() {
        val config: CafeNotifierConfig = CafeNotifierConfig()
        config.load()
        val cs: CafeSearch = CafeSearch(config)
        val result: CafeSearchResult = cs.search("test")
        assertNotNull(result)
        assertEquals(20, result.size)
        for (article in result.articles) {
            println("article: $article")
        }
    }
}