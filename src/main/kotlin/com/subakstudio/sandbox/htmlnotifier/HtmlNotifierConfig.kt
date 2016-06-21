package com.subakstudio.sandbox.htmlnotifier

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.subakstudio.sandbox.cafenotifier.CafeNotifierException
import java.io.File

/**
 * Created by yeoupooh on 16. 6. 21.
 */
class HtmlNotifierConfig {
    var token: String? = null
    var url: String? = null
    var telegramIntervalMs: Long = 0
    var actionIntervalMs: Long = 0
    private val configFile = File(System.getProperty("user.home"), ("html.notifier.config.json"))

    fun load() {
        if (configFile.exists()) {
            val om: ObjectMapper = ObjectMapper()
            val rootNode: JsonNode = om.readTree(configFile)
            token = rootNode.get("telegram").get("token").asText()
            telegramIntervalMs = rootNode.get("telegram").get("intervalSec").asLong() * 1000
            url = rootNode.get("html").get("url").asText()
            actionIntervalMs = rootNode.get("html").get("intervalSec").asLong() * 1000
            println("HtmlNotifierConfig: $rootNode, $token")
        } else {
            throw CafeNotifierException("Can't load config file.")
        }
    }
}