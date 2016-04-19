package com.subakstudio.sandbox.cafenotifier.search

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import com.subakstudio.sandbox.cafenotifier.CafeNotifierConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * Created by yeoupooh on 16. 4. 19.
 */
class CafeSearch(val config: CafeNotifierConfig) {
    fun search(keyword: String): CafeSearchResult {
        var result: CafeSearchResult = CafeSearchResult()
        var client: OkHttpClient = OkHttpClient()
        val url = "http://m.cafe.naver.com/ArticleSearchList.nhn?search.query=${keyword}&search.menuid=&search.searchBy=0&search.sortBy=date&search.clubid=${config.clubId}"
        var request: Request = Request.Builder()
                .url(url)
                // http://www.useragentstring.com/Chrome41.0.2228.0_id_19841.php
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .build()
        var response: Response = client.newCall(request).execute()
        var doc: Document = Jsoup.parse(response.body().string())
        var elements: Elements = doc.select("#articleList li")
        //        println("elements: $elements")
        for (ele in elements) {
            var href: String = ele.select("a").attr("href")
            var onclick: String = ele.select("a").attr("onclick")
            var re: Regex = Regex("nclk\\(this, 'cfs\\*n.list', '([0-9]*)', ''\\)")
            var m = re.find(onclick)
            var id: Int = 0
            if (m?.groups?.size!! > 1) {
                id = m?.groupValues?.get(1)!!.toInt()
                println("groups: $id")
            }
            var title: String = ele.select("h3").text()
            var text: String = ele.select(".post_area").text()
            var name: String = ele.select(".name").text()
            var time: String = ele.select(".time").text()
            var views: Int = ele.select(".no em").text().toInt()
            println("ele: $title href=[$href]")
            result.add(CafeArticle(id, title, name, time, views, "http://m.cafe.naver.com/$href", text))
        }
        return result
    }
}