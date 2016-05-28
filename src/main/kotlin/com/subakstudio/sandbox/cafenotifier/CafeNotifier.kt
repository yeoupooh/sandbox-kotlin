package com.subakstudio.sandbox.cafenotifier

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.TelegramBotAdapter
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.model.request.ReplyKeyboardHide
import com.pengrad.telegrambot.response.GetUpdatesResponse
import com.subakstudio.sandbox.cafenotifier.search.CafeSearch
import com.subakstudio.sandbox.cafenotifier.search.CafeSearchResult
import retrofit.RetrofitError
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by yeoupooh on 4/19/16.
 */
open class CafeNotifier {

    private var bot: TelegramBot? = null
    private var data: CafeNotifierData = CafeNotifierData()
    private var config: CafeNotifierConfig = CafeNotifierConfig()
    private var search: CafeSearch? = null
    private var lastTelegramUpdateMs: Long = System.currentTimeMillis()
    private var lastCafeUpdateMs: Long = System.currentTimeMillis()

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val app: CafeNotifier = CafeNotifier()
            try {
                app.init()
                app.start()
            } catch(e: CafeNotifierException) {
                System.err?.println("Failed to run CafeNotifier due to ${e.cause}")
            }
        }
    }

    private fun init() {
        data.load()
        config.load()
        search = CafeSearch(config)
        bot = TelegramBotAdapter.build(config.token)
    }

    private fun start() {
        thread() {
            while (true) {
                if (System.currentTimeMillis() - lastTelegramUpdateMs > config.telegramIntervalMs) {
                    updateChats()
                    lastTelegramUpdateMs = System.currentTimeMillis()
                }

                if (System.currentTimeMillis() - lastCafeUpdateMs > config.cafeIntervalMs) {
                    updateCafe()
                    lastCafeUpdateMs = System.currentTimeMillis()
                }

                Thread.sleep(500)
            }
        }
    }

    private fun updateChats() {
        var response: GetUpdatesResponse
        var messages = ArrayList<Message>()
        // earliest
        val offset = 0
        // 1-100, default: 100
        val limit = 100
        // seconds, default: 0
        val timeout = 0
        try {
            response = bot?.getUpdates(offset, limit, timeout) as GetUpdatesResponse
        } catch (e: RetrofitError) {
            System.err?.println(e.message)
            return
        }
        response.let {
            if (response.isOk) {
                var foundNew: Boolean = false
                for (update in response.updates()) {
                    if (update.updateId() > data.chatLastUpdateId) {
                        println("updateChats: new update: $update")
                        data.chatLastUpdateId = update.updateId()
                        messages.add(update.message())
                        foundNew = true
                    }
                }
                if (foundNew) {
                    // Save updated messages
                    data.save()
                    for (message in messages) {
                        if (message.text().startsWith("/start ")) {
                            var keyword = message.text().substring(7)
                            if (data.containsChatId(message.chat().id())) {
                                bot?.sendMessage(message.chat().id(), "change to monitor ${data.getKeyword(message.chat().id())} to $keyword")
                            } else {
                                bot?.sendMessage(message.chat().id(), "start to monitor $keyword")
                            }
                            data.add(message.chat().id(), keyword)
                            // Save for new keyword
                            data.save()
                        } else if (message.text().equals("/stop")) {
                            var user = data.users.get(message.chat().id())
                            bot?.sendMessage(message.chat().id(), "stop to monitor ${user?.keyword}")
                            data.remove(message.chat().id())
                            // Save for removed keyword
                            data.save()
                        } else {
                            bot?.sendMessage(message.chat().id(), "Unknown command: ${message.text()}")
                        }
                    }
                } else {
                    println("updateChats: no new chats.")
                }
            }
        }
    }

    private fun updateCafe() {
        var foundNewArticle: Boolean = false
        if (data.users.size == 0) {
            println("updateCafe: no users")
            return
        }
        for (user in data.users.values) {
            var searchResult: CafeSearchResult = search?.search(user.keyword)!!
            for (article in searchResult.articles) {
                if (user.lastArticleId < article.id) {
                    bot?.sendMessage(user.chatId, "*${article.title}* by *${article.name}*\n\n[${article.href}](${article.href})", ParseMode.Markdown, false, 0, ReplyKeyboardHide())
                    user.lastArticleId = article.id
                    foundNewArticle = true
                    println("updateCafe: ${article.title} by ${article.name} ${article.href}")
                }
            }
        }
        if (foundNewArticle) {
            data.save()
        } else {
            println("updateCafe: no new articles.")
        }
    }
}

