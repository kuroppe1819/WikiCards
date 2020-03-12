package com.card.wiki.moyashi.wikicards.parameter

import android.util.Log

class ViewSettings() {

    fun getViewSize(title: String): Float {
        val length = title.length
//        Log.d(TAG, "titleLength : ${length}")
        var size = 32.0f
        when {
            11 > length && length > 8 || 21 > length && length > 16 -> size = 28.0f
            length >= 21 -> size = 24.0f
            13 > length && length > 10 && title.contains("(") == true -> size = 28.0f
        }
        return size
    }

    fun getSubArticle(article: String): String {
        val length = article.length
//        Log.d(TAG, "articleLength : ${length}")
        when {
            length >= 195 -> {
                return article.substring(0, 195) + "..."
            }
        }
        return article
    }

    companion object {
        private val TAG = "ViewSettings"
    }
}
