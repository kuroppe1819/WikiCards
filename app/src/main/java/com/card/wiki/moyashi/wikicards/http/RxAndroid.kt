package com.card.wiki.moyashi.wikicards.http

import android.util.Log
import com.card.wiki.moyashi.wikicards.RxCallbacks
import com.card.wiki.moyashi.wikicards.parameter.ItemData
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.*

class RxAndroid() : Subscriber<Response>() {
    lateinit var type: String
    lateinit var itemList: ArrayList<ItemData>
    var onRxCallback: RxCallbacks? = null

    fun setCallback(onRxCallback: RxCallbacks) {
        this.onRxCallback = onRxCallback
    }

    private fun toArrayList(items: JSONArray): ArrayList<String> {
        val arrayList = ArrayList<String>()
        for (i in 0..items.length() - 1) {
            arrayList.add(items.getJSONObject(i).get("id").toString())
        }
        arrayList.forEach { Log.d(TAG, it) }
        return arrayList
    }

    private fun toPageidParameter(): String {
        val idParam = StringBuilder()
        idList!!.forEach { idParam.append("|${it}") }
        return idParam.toString()
    }

    private fun SettingBuilder(): HttpUrl {
        val httpUri: HttpUrl
        val builder = HttpUrl.Builder()
                .scheme("https")
                .host("ja.wikipedia.org")
                .addPathSegments("w/api.php")
                .addQueryParameter("format", "json")
                .addQueryParameter("action", "query")

        when (type) {
            "title" -> {
                httpUri = builder.addQueryParameter("list", "random")
                        .addQueryParameter("titles", "&utf8")
                        .addQueryParameter("rnnamespace", "0")
                        .addQueryParameter("rnlimit", "20")
                        .build()
            }
            else -> {
                httpUri = builder.addQueryParameter("prop", "extracts")
                        .addQueryParameter("exlimit", "max")
                        .addQueryParameter("exintro", "")
                        .addQueryParameter("explaintext", "")
                        .addQueryParameter("utf8", "")
                        .addQueryParameter("pageids", toPageidParameter())
                        .build()
            }
        }
        return httpUri
    }

    fun onHttpConnect(type: String) {
        this.type = type
        Observable
                .create(Observable.OnSubscribe<Response> { subscriber ->
                    try {
                        val wikiUrl = SettingBuilder()
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
        when (type) {
            "title" -> onRxCallback?.getTitleCompleted()
            else -> {
                onRxCallback?.getArticleCompleted(itemList)
            }
        }
    }

    override fun onError(e: Throwable) {
        Log.d(TAG, "onError: " + e)
    }

    override fun onNext(response: Response) {
        val responseJson = JSONObject(response.body().string())
        when (type) {
            "title" -> {
                idList?.clear()
                idList = toArrayList(responseJson.getJSONObject("query").getJSONArray("random"))
            }
            else -> {
                itemList = ArrayList<ItemData>()

                val pageData = responseJson
                        .getJSONObject("query")
                        .getJSONObject("pages")

                idList!!.forEach {
                    val itemData = ItemData()
                    itemData.titleText = pageData.getJSONObject(it).get("title").toString()
                    itemData.articleText = pageData.getJSONObject(it).get("extract").toString()
                    itemList.add(itemData)
                }
            }
        }
    }

    companion object {
        private val TAG = "RxAndroid"
        private var idList: ArrayList<String>? = null
        private val ERRORMESSAGE = "しばらく時間がたってから再接続してください"
    }
}
