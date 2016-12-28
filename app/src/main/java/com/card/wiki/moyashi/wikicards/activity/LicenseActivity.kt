package com.card.wiki.moyashi.wikicards.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import com.card.wiki.moyashi.wikicards.R
import android.webkit.WebView



class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
        val webView = findViewById(R.id.license_web_view) as WebView
        webView.setWebChromeClient(WebChromeClient())
        webView.loadUrl("file:///android_asset/licenses.html")
    }
}