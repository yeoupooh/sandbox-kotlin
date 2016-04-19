package com.subakstudio.sandbox.cafenotifier

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.TelegramBotAdapter
import com.pengrad.telegrambot.response.GetUpdatesResponse
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

/**
 * Created by yeoupooh on 4/19/16.
 */
open class CafeNotifier {

    private var bot: TelegramBot? = null
    private var latestUpdateId = 0;
    private val dataFile = File(System.getProperty("user.home"), ("cafenotifier.data.json"))

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val app: CafeNotifier = CafeNotifier()
            app.init()
            app.start()
        }
    }

    private fun init() {
        val configFile = File(this.javaClass.getResource("/cafenotifier.config.json").file)
        val om: ObjectMapper = ObjectMapper()
        val rootNode: JsonNode = om.readTree(configFile)
        val token: String = rootNode.get("telegram").get("token").asText()
        println("$rootNode, $token")

        loadDataFile()

        bot = TelegramBotAdapter.build(token)
    }

    private fun loadDataFile() {
        if (dataFile.exists()) {
            val om: ObjectMapper = ObjectMapper()
            val rootNode: JsonNode = om.readTree(dataFile)
            latestUpdateId = rootNode.get("lastUpdatedId").asInt()
            println("loadDataFile: latestUpdateId: $latestUpdateId")
        } else {
            saveDataFile()
        }
    }

    private fun saveDataFile() {
        val om: ObjectMapper = ObjectMapper()
        var rootNode: ObjectNode = om.createObjectNode()
        rootNode.put("lastUpdatedId", latestUpdateId)
        var gen: JsonGenerator = JsonFactory().createGenerator(FileOutputStream(dataFile))
        om.writeTree(gen, rootNode);
        println("saveDataFile: data file saved.")
    }

    private fun start() {
        var response: GetUpdatesResponse

        thread() {
            while (true) {
                // FIXME Retrofit is cacheing updates. So once get update, no updates next time.
                response = bot?.getUpdates(10, 10, 5000) as GetUpdatesResponse
                response.let {
                    if (response.isOk) {
                        var foundNew: Boolean = false
                        for (update in response.updates()) {
                            if (update.updateId() > latestUpdateId) {
                                println("start: new update: $update")
                                latestUpdateId = update.updateId()
                                foundNew = true
                            }
                        }
                        if (foundNew) {
                            saveDataFile()
                        } else {
                            println("start: no updates.")
                        }
                    }
                }
                Thread.sleep(1000)
            }
        }
    }
}