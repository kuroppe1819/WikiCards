package com.card.wiki.moyashi.wikicards

import com.card.wiki.moyashi.wikicards.http.ItemData
import java.util.*

interface RxCallbacks {
    fun getTitleCompleted(idList: ArrayList<String>)
    fun getArticleCompleted(itemData: ItemData)
}
