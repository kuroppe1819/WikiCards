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

    fun onSaveCount(count: Int) {
        preferences.edit().putInt("count", count).commit()
    }

    fun onGetCount(): Int{
        return preferences.getInt("count", 0)
    }

    fun preferenseDelete() {
        preferences.edit().clear().commit()
    }


}