package com.card.wiki.moyashi.wikicards.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.card.wiki.moyashi.wikicards.R
import com.card.wiki.moyashi.wikicards.http.CustomTabs

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val customTabs = CustomTabs(this, "https://ja.wikipedia.org/wiki/ガーリッシュナンバー")
        customTabs.onWarmUp()
        customTabs.onStartUp()
    }
}
