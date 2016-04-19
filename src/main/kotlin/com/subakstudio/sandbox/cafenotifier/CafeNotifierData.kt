package com.subakstudio.sandbox.cafenotifier

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.File
import java.io.FileOutputStream

/**
 * Created by yeoupooh on 4/19/16.
 */

class CafeNotifierData(var latestUpdateId: Int = 0) {
    private val dataFile = File(System.getProperty("user.home"), ("cafenotifier.data.json"))

    fun load() {
        if (dataFile.exists()) {
            val om: ObjectMapper = ObjectMapper()
            val rootNode: JsonNode = om.readTree(dataFile)
            latestUpdateId = rootNode.get("lastUpdatedId").asInt()
            println("loadDataFile: latestUpdateId: $latestUpdateId")
        } else {
            save()
        }
    }

    fun save() {
        val om: ObjectMapper = ObjectMapper()
        var rootNode: ObjectNode = om.createObjectNode()
        rootNode.put("lastUpdatedId", latestUpdateId)
        var gen: JsonGenerator = JsonFactory().createGenerator(FileOutputStream(dataFile))
        om.writeTree(gen, rootNode);
        println("saveDataFile: data file saved.")
    }
}