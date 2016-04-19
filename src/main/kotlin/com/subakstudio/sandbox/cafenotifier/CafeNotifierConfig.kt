package com.subakstudio.sandbox.cafenotifier

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

/**
 * Created by yeoupooh on 16. 4. 19.
 */
class CafeNotifierConfig() {
    var token: String? = null

    fun load() {
        val configUrl = this.javaClass.getResource("/cafenotifier.config.json")
        if (configUrl != null) {
            val configFile = File(this.javaClass.getResource("/cafenotifier.config.json").file)
            val om: ObjectMapper = ObjectMapper()
            val rootNode: JsonNode = om.readTree(configFile)
            token = rootNode.get("telegram").get("token").asText()
            println("$rootNode, $token")
        } else {
            throw CafeNotifierException("can't load config file.")
        }
    }
}