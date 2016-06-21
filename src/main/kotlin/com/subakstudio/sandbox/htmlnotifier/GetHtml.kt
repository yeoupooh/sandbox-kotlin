package com.subakstudio.sandbox.htmlnotifier

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

/**
 * Created by yeoupooh on 16. 6. 21.
 */
class GetHtml(val config: HtmlNotifierConfig) {
    fun action() {
        var client: OkHttpClient = OkHttpClient()
        val url = config.url
        println("url: $url")
        var request: Request = Request.Builder()
                .url(url)
                // http://www.useragentstring.com/Chrome41.0.2228.0_id_19841.php
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .build()
        try {
            var response: Response = client.newCall(request).execute()
            var doc: Document = Jsoup.parse(response.body().string())
//            println("$doc")
            var content = doc.select("div.content").text()
            println("$content")
        } catch(e: IOException) {
            System.err?.println(e.message)
        }
    }
}