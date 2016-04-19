package com.subakstudio.sandbox.cafenotifier

/**
 * Created by yeoupooh on 16. 4. 20.
 */
data class CafeNotifierUser(var chatId: Long, var keyword: String) {
    var lastArticleId: Int = 0
}