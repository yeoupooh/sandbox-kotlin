package com.subakstudio.sandbox.cafenotifier

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by yeoupooh on 4/19/16.
 */

class CafeNotifierData(var chatLastUpdateId: Int = 0, var cafeLastUpdatedId: Int = 0) {
    var users = HashMap<Long, CafeNotifierUser>()
    private val dataFile = File(System.getProperty("user.home"), ("cafenotifier.data.json"))

    fun load() {
        if (dataFile.exists()) {
            val om: ObjectMapper = ObjectMapper()
            val rootNode: JsonNode = om.readTree(dataFile)
            chatLastUpdateId = rootNode.get("lastUpdatedId").asInt()
            val usersNode: JsonNode? = rootNode.get("users")
            usersNode?.let {
                for (userNode in usersNode?.elements()!!) {
                    var user: CafeNotifierUser = CafeNotifierUser(userNode.get("chatId").asLong(), userNode.get("keyword").asText())
                    users.put(user.chatId, user)
                }
            }
            println("loadDataFile: latestUpdateId: $chatLastUpdateId")
        } else {
            save()
        }
    }

    fun save() {
        val om: ObjectMapper = ObjectMapper()
        var rootNode: ObjectNode = om.createObjectNode()
        rootNode.put("lastUpdatedId", chatLastUpdateId)
        var usersNode: ArrayNode = om.createArrayNode()
        for (user in users.values) {
            var userNode: ObjectNode = om.createObjectNode()
            userNode.put("chatId", user.chatId)
            userNode.put("keyword", user.keyword)
            usersNode.add(userNode)
        }
        rootNode.set("uesrs", usersNode)
        var gen: JsonGenerator = JsonFactory().createGenerator(FileOutputStream(dataFile))
        om.writeTree(gen, rootNode);
        println("saveDataFile: data file saved.")
    }

    fun add(chatId: Long, keyword: String) {
        users.put(chatId, CafeNotifierUser(chatId, keyword))
    }

    fun remove(id: Long) {
        users.remove(id)
    }
}