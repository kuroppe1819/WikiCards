package com.card.wiki.moyashi.wikicards.http

import android.app.Activity

import android.graphics.BitmapFactory

import android.net.Uri

import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.v4.content.ContextCompat
import com.card.wiki.moyashi.wikicards.R
import org.chromium.customtabsclient.shared.CustomTabsHelper
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsSession
import android.text.TextUtils
import android.util.Log
import org.chromium.customtabsclient.shared.ServiceConnection
import org.chromium.customtabsclient.shared.ServiceConnectionCallback


class CustomTabs(activity: Activity, url: String) : ServiceConnectionCallback {
    override fun onServiceConnected(client: CustomTabsClient?) {
        Log.d(TAG, "onServiceConnected")
        mClient = client
    }

    override fun onServiceDisconnected() {
        Log.d(TAG, "onServiceDisconnected")
        mClient = null
    }

    private val TAG = "ChromeCustomTabs"
    private var activity: Activity
    private var url: String
    private var mConnection: CustomTabsServiceConnection? = null
    private var mClient: CustomTabsClient? = null
    private var mSession: CustomTabsSession? = null
    private var mPackageName: String? = null

    init {
        this.activity = activity
        this.url = url
    }

    private fun bindCustomTabsService() {
        if (mClient != null) return
        if (TextUtils.isEmpty(mPackageName)) {
            mPackageName = CustomTabsHelper.getPackageNameToUse(activity)
            if (mPackageName == null) return
        }
        mConnection = ServiceConnection(this)
        val ok = CustomTabsClient.bindCustomTabsService(activity, mPackageName, mConnection)
        if (!ok) mConnection = null
    }

    fun unbindCustomTabsService() {
        if (mConnection == null) return
        activity.unbindService(mConnection)
        mClient = null
        mSession = null
    }

    private fun getSession(): CustomTabsSession? {
        if (mSession == null) {
            mSession = mClient?.newSession(null)
        }
        return mSession
    }

    fun onWarmUp() {
        bindCustomTabsService()
        if (mClient != null) {
            (mClient as CustomTabsClient).warmup(0)
            val session = getSession()
            val success = session?.mayLaunchUrl(Uri.parse(url), null, null)
            Log.d(TAG, "mayLaunchUrl : ${success}")
        }
    }

    fun onStartUp() {
        val builder = CustomTabsIntent.Builder(getSession())
        val customTabsIntent = builder.build()

        builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark)).setShowTitle(true)
        builder.setStartAnimations(activity, R.anim.slide_in_right, R.anim.slide_out_left)
        builder.setExitAnimations(activity, R.anim.slide_in_left, R.anim.slide_out_right)
        builder.setCloseButtonIcon(
                BitmapFactory.decodeResource(activity.resources, R.drawable.ic_arrow_back))

        customTabsIntent.intent.setPackage(mPackageName)
        CustomTabsHelper.addKeepAliveExtra(activity, customTabsIntent.intent)
        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }
}