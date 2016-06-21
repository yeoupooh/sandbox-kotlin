package com.subakstudio.sandbox.htmlnotifier

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.TelegramBotAdapter
import com.subakstudio.sandbox.cafenotifier.CafeNotifierException
import kotlin.concurrent.thread

/**
 * Created by yeoupooh on 16. 6. 21.
 */
class HtmlNotifier {

    private var bot: TelegramBot? = null
    private var config: HtmlNotifierConfig = HtmlNotifierConfig()
    private var lastTelegramUpdateMs: Long = System.currentTimeMillis()
    private var lastActionUpdateMs: Long = System.currentTimeMillis()

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val app: HtmlNotifier = HtmlNotifier()
            try {
                app.init()
                app.start()
            } catch(e: CafeNotifierException) {
                System.err?.println("Failed to run HtmlNotifier due to ${e.cause}")
            }
        }
    }

    private fun init() {
        config.load()
        bot = TelegramBotAdapter.build(config.token)
    }

    private fun start() {
        thread() {
            while (true) {
                if (System.currentTimeMillis() - lastTelegramUpdateMs > config.telegramIntervalMs) {
                    updateChats()
                    lastTelegramUpdateMs = System.currentTimeMillis()
                }

                if (System.currentTimeMillis() - lastActionUpdateMs > config.actionIntervalMs) {
                    updateAction()
                    lastActionUpdateMs = System.currentTimeMillis()
                }

                Thread.sleep(500)
            }
        }
    }

    private fun updateAction() {
        println("updateAction")
        GetHtml(config).action()
    }

    private fun updateChats() {
        println("updateChats")
    }
}