package com.card.wiki.moyashi.wikicards

import java.util.*

interface RxCallbacks {
    fun getTitleCompleted(titleList: ArrayList<String>)
    fun getArticleCompleted(article: String)
}
