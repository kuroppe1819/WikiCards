package com.card.wiki.moyashi.wikicards.http

import android.util.Log
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

class RxAndroid(onRxCallback: RxCallbacks) : Subscriber<Response>() {
    lateinit var titleList: ArrayList<String>
    lateinit var getCategory: String
    var onRxCallback: RxCallbacks

    init {
        this.onRxCallback = onRxCallback
    }

    private fun SettingBuilder(getCategory: String): HttpUrl {
        val httpUri: HttpUrl
        when (getCategory) {
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
                        .addQueryParameter("prop", "revisions")
                        .addQueryParameter("titles", getCategory)
                        .addEncodedQueryParameter("utf8", "")
                        .addEncodedQueryParameter("rvprop", "content")
                        .addEncodedQueryParameter("rvparse", "")
                        .build()
            }
        }
        return httpUri
    }

    private fun toArrayList(items: JSONArray): ArrayList<String> {
        val arrayList = ArrayList<String>()
        for (i in 0..items.length() - 1) {
            arrayList.add(items.getJSONObject(i).get("title").toString())
        }
        return arrayList
    }

    fun onHttpConnect(getCategory: String) {
        this.getCategory = getCategory
        Observable
                .create(Observable.OnSubscribe<Response> { subscriber ->
                    try {
                        val wikiUrl = SettingBuilder(getCategory)
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
        when (getCategory){
            "title" -> onRxCallback.getTitleCompleted(titleList)
        }
    }

    override fun onError(e: Throwable) {
        Log.d(TAG, "onError: " + e)
    }

    override fun onNext(response: Response) {
        val responseJson = JSONObject(response.body().string())
        when (getCategory){
            "title" -> titleList = toArrayList(responseJson.getJSONObject("query").getJSONArray("random"))
            else -> {
                val html = responseJson
                        .getJSONObject("query")
                        .getJSONObject("pages")
                        .getJSONObject("12696")
                        .getJSONArray("revisions")
                        .get(0).toString()
                val article = Jsoup.parse(html)
                Log.d(TAG, article.getElementsByTag("p").first().toString())
            }
        }
    }

    companion object {
        private val TAG = "RxAndroid"
        private val ERRORMESSAGE = "しばらく時間がたってから再接続してください"
    }
}
