package com.subakstudio.sandbox.cafenotifier

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

/**
 * Created by yeoupooh on 16. 4. 19.
 */
class CafeNotifierConfig() {
    var token: String? = null
    var clubId: Long = 0
    var telegramIntervalMs: Long = 0
    var cafeIntervalMs: Long = 0
    private val configFile = File(System.getProperty("user.home"), ("cafenotifier.config.json"))

    fun load() {
        if (configFile.exists()) {
            val om: ObjectMapper = ObjectMapper()
            val rootNode: JsonNode = om.readTree(configFile)
            token = rootNode.get("telegram").get("token").asText()
            telegramIntervalMs = rootNode.get("telegram").get("intervalSec").asLong() * 1000
            clubId = rootNode.get("cafe").get("clubid").asLong()
            cafeIntervalMs = rootNode.get("cafe").get("intervalSec").asLong() * 1000
            println("CafeNotifierConfig: $rootNode, $token")
        } else {
            throw CafeNotifierException("Can't load config file.")
        }
    }

}