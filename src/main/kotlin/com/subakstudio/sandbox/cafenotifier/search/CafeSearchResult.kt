package com.subakstudio.sandbox.cafenotifier.search

import java.util.*

/**
 * Created by yeoupooh on 16. 4. 19.
 */
class CafeSearchResult {
    var articles = ArrayList<CafeArticle>()
    var size: Int = 0
        get() = articles.size

    fun add(article: CafeArticle) {
        articles.add(article)
    }
}