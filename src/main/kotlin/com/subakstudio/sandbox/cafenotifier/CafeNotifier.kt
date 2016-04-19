package com.subakstudio.sandbox.cafenotifier

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.TelegramBotAdapter
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.response.GetUpdatesResponse
import java.io.File
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by yeoupooh on 4/19/16.
 */
open class CafeNotifier {

    private var bot: TelegramBot? = null
    private var data: CafeNotifierData = CafeNotifierData()

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

        data.load()

        bot = TelegramBotAdapter.build(token)
    }

    private fun start() {
        var response: GetUpdatesResponse

        thread() {
            while (true) {
                var messages = ArrayList<Message>()
                // earliest
                val offset = 0
                // 1-100, default: 100
                val limit = 100
                // seconds, default: 0
                val timeout = 0
                response = bot?.getUpdates(offset, limit, timeout) as GetUpdatesResponse
                response.let {
                    if (response.isOk) {
                        var foundNew: Boolean = false
                        for (update in response.updates()) {
                            if (update.updateId() > data.latestUpdateId) {
                                println("start: new update: $update")
                                data.latestUpdateId = update.updateId()
                                messages.add(update.message())
                                foundNew = true
                            }
                        }
                        if (foundNew) {
                            data.save()
                            for (message in messages) {
                                bot?.sendMessage(message.chat().id(), "message from bot: ${message.from().username()} said ${message.text()}")
                            }
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

