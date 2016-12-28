package com.card.wiki.moyashi.wikicards

import java.util.*

interface RxCallbacks {
    fun getTitleCompleted(idList: ArrayList<String>)
    fun getArticleCompleted(title: String, article: String)
}
