package com.subakstudio.sandbox.cafenotifier.search

import com.subakstudio.sandbox.cafenotifier.CafeNotifierConfig
import org.apache.commons.io.IOUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test

/**
 * Created by yeoupooh on 16. 4. 19.
 */
class CafeSearchTest {
    // NOTE To make this success, need to have proper values in config file
    @Ignore
    @Test
    fun search() {
        val config: CafeNotifierConfig = CafeNotifierConfig()
        config.load()
        val cs: CafeSearch = CafeSearch(config)
        val result: CafeSearchResult = cs.search("a b")
        assertNotNull(result)
        assertEquals(20, result.size)
        for (article in result.articles) {
            println("article: $article")
        }
    }

    @Test
    fun parseResult() {
        val html = IOUtils.toString(this.javaClass.getResource("/search-result.htm"), "utf-8")
        assertNotNull(html)
        val config: CafeNotifierConfig = CafeNotifierConfig()
        val cs: CafeSearch = CafeSearch(config)
        var result: CafeSearchResult = cs.parseResult(html)
        assertNotNull(result)
        assertEquals(20, result.size)
        for (article in result.articles) {
            println("article: $article")
        }
    }
}