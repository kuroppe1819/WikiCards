package com.card.wiki.moyashi.wikicards.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.card.wiki.moyashi.wikicards.R
import com.card.wiki.moyashi.wikicards.RxCallbacks
import com.card.wiki.moyashi.wikicards.http.CustomTabs
import com.card.wiki.moyashi.wikicards.http.RxAndroid
import java.util.*

class MainActivity : AppCompatActivity(), RxCallbacks {
    var titleList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val customTabs = CustomTabs(this, "https://ja.wikipedia.org/wiki/ガーリッシュナンバー")
//        customTabs.onWarmUp()
//        customTabs.onStartUp()

        RxAndroid(this).onHttpConnect("ガーリッシュナンバー")

    }

    override fun getTitleCompleted(titleList: ArrayList<String>) {
        this.titleList = titleList
        titleList.forEach { Log.d(TAG, it) }
    }

    override fun getArticleCompleted(article: String) {

    }

    companion object {
        private val TAG = "MainActivity"
    }
}
