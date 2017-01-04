package com.card.wiki.moyashi.wikicards.http

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent

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
        this.url = "https://ja.wikipedia.org/wiki/${url}"
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

    private fun SettingShareIntent() : PendingIntent{
        val intent = Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, url)

        return PendingIntent.getActivity(activity, 0, intent, 0)
    }

    private fun getSession(): CustomTabsSession? {
        if (mSession == null) {
            mSession = mClient?.newSession(null)
        }
        return mSession
    }

    fun unbindCustomTabsService() {
        if (mConnection == null) return
        activity.unbindService(mConnection)
        mClient = null
        mSession = null
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

        builder.setToolbarColor(ContextCompat.getColor(activity, R.color.DarkGray)).setShowTitle(true)
        builder.setCloseButtonIcon(
                BitmapFactory.decodeResource(activity.resources, R.drawable.ic_arrow_back))
        builder.addDefaultShareMenuItem()
        builder.setActionButton(
                BitmapFactory.decodeResource(activity.resources, R.drawable.ic_share), "share", SettingShareIntent())

        customTabsIntent.intent.setPackage(mPackageName)
        CustomTabsHelper.addKeepAliveExtra(activity, customTabsIntent.intent)
        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }
}