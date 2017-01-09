package com.card.wiki.moyashi.wikicards

import com.card.wiki.moyashi.wikicards.parameter.ItemData
import java.util.*

interface RxCallbacks {
    fun getTitleCompleted()
    fun getArticleCompleted(itemList: ArrayList<ItemData>)
}
