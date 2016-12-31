package com.card.wiki.moyashi.wikicards.preference

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Preferences(activity: Activity) {
    var preferences: SharedPreferences

    init {
        this.preferences = activity.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
    }

    fun onSaveCount(count: Long) {
        preferences.edit().putLong("count", count).commit()
    }

    fun onGetCount(): Long {
        return preferences.getLong("count", 0)
    }

    fun onSaveTitle(title: String){
        preferences.edit().putString("title", title).commit()
    }

    fun onGetTitle(): String? {
        return preferences.getString("title", "")
    }

    fun preferenseDelete() {
        preferences.edit().clear().commit()
    }


}