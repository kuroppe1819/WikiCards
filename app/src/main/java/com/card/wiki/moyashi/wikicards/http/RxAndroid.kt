package com.card.wiki.moyashi.wikicards.http

import android.app.Activity
import android.util.Log
import com.card.wiki.moyashi.wikicards.R
import com.card.wiki.moyashi.wikicards.RxCallbacks
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.*

class RxAndroid() : Subscriber<Response>() {
    lateinit var idList: ArrayList<String>
    lateinit var id: String
    lateinit var title: String
    lateinit var article: String
    var onRxCallback: RxCallbacks? = null

//    init {
//        this.onRxCallback = onRxCallback
//    }

    fun setCallback(onRxCallback: RxCallbacks) {
        this.onRxCallback = onRxCallback
    }

    private fun SettingBuilder(id: String): HttpUrl {
        val httpUri: HttpUrl
        when (id) {
            "title" -> {
                httpUri = HttpUrl.Builder()
                        .scheme("https")
                        .host("ja.wikipedia.org")
                        .addPathSegments("w/api.php")
                        .addQueryParameter("format", "json")
                        .addQueryParameter("action", "query")
                        .addQueryParameter("list", "random")
                        .addQueryParameter("titles", "&utf8")
                        .addQueryParameter("rnnamespace", "0")
                        .addQueryParameter("rnlimit", "5")
                        .build()
            }
            else -> {
                httpUri = HttpUrl.Builder()
                        .scheme("https")
                        .host("ja.wikipedia.org")
                        .addPathSegments("w/api.php")
                        .addQueryParameter("format", "json")
                        .addQueryParameter("action", "query")
                        .addQueryParameter("prop", "extracts")
                        .addEncodedQueryParameter("exintro", "")
                        .addEncodedQueryParameter("explaintext", "")
                        .addQueryParameter("pageids", id)
                        .addEncodedQueryParameter("utf8", "")
                        .build()
            }
        }
        return httpUri
    }

    private fun toArrayList(items: JSONArray): ArrayList<String> {
        val arrayList = ArrayList<String>()
        for (i in 0..items.length() - 1) {
            arrayList.add(items.getJSONObject(i).get("id").toString())
        }
        return arrayList
    }

    fun onHttpConnect(id: String) {
        this.id = id
        Observable
                .create(Observable.OnSubscribe<Response> { subscriber ->
                    try {
                        val wikiUrl = SettingBuilder(id)
                        Log.d(TAG, wikiUrl.toString())

                        val request = Request.Builder()
                                .url(wikiUrl)
                                .get()
                                .cacheControl(CacheControl.FORCE_NETWORK)
                                .build()

                        /** リクエスト開始 **/
                        val response = OkHttpClient()
                                .newCall(request)
                                .execute()

                        subscriber.onNext(response)
                        subscriber.onCompleted()
                    } catch (e: IOException) {
                        subscriber.onError(e)
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this)
    }

    override fun onCompleted() {
        Log.d(TAG, "onCompleted")
        when (id) {
            "title" -> onRxCallback?.getTitleCompleted(idList)
            else -> {
                val itemData = ItemData()
                itemData.titleText = title
                itemData.articleText = article
                onRxCallback?.getArticleCompleted(itemData)
            }
        }
    }

    override fun onError(e: Throwable) {
        Log.d(TAG, "onError: " + e)
    }

    override fun onNext(response: Response) {
        val responseJson = JSONObject(response.body().string())
        when (id) {
            "title" -> idList = toArrayList(responseJson.getJSONObject("query").getJSONArray("random"))
            else -> {
                val pageData = responseJson
                        .getJSONObject("query")
                        .getJSONObject("pages")
                        .getJSONObject(id)
                title = pageData.get("title").toString()
                article = pageData.get("extract").toString()
            }
        }
    }

    companion object {
        private val TAG = "RxAndroid"
        private val ERRORMESSAGE = "しばらく時間がたってから再接続してください"
    }
}
