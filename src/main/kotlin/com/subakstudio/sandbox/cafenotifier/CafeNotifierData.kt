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
    val FIELD_USERS = "uesrs"
    val FIELD_LAST_UPDATED_ID = "lastUpdatedId"
    val FIELD_CHAT_ID = "chatId"
    val FIELD_KEYWORD = "keyword"
    val FIELD_LAST_ARTICLE_ID = "lastArticleId"
    var users = HashMap<Long, CafeNotifierUser>()
    private val dataFile = File(System.getProperty("user.home"), ("cafenotifier.data.json"))

    fun load() {
        if (dataFile.exists()) {
            val om: ObjectMapper = ObjectMapper()
            val rootNode: JsonNode = om.readTree(dataFile)
            chatLastUpdateId = rootNode.get(FIELD_LAST_UPDATED_ID).asInt()
            val usersNode: JsonNode? = rootNode.get(FIELD_USERS)
            println("load: chatLastUpdateId=$chatLastUpdateId")
            println("load: usersNode=$usersNode")
            usersNode?.let {
                for (userNode in usersNode.elements()!!) {
                    var user: CafeNotifierUser = CafeNotifierUser(
                            userNode.get(FIELD_CHAT_ID).asLong(),
                            userNode.get(FIELD_KEYWORD).asText(),
                            if (userNode.get(FIELD_LAST_ARTICLE_ID) != null)
                                userNode.get(FIELD_LAST_ARTICLE_ID).asLong()
                            else 0)
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
        rootNode.put(FIELD_LAST_UPDATED_ID, chatLastUpdateId)
        var usersNode: ArrayNode = om.createArrayNode()
        for (user in users.values) {
            var userNode: ObjectNode = om.createObjectNode()
            userNode.put(FIELD_CHAT_ID, user.chatId)
            userNode.put(FIELD_KEYWORD, user.keyword)
            userNode.put(FIELD_LAST_ARTICLE_ID, user.lastArticleId)
            usersNode.add(userNode)
        }
        rootNode.set(FIELD_USERS, usersNode)
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

    fun containsChatId(chatId: Long): Boolean {
        return users.containsKey(chatId)
    }

    fun getKeyword(chatId: Long?): String? {
        return users[chatId]?.keyword
    }
}