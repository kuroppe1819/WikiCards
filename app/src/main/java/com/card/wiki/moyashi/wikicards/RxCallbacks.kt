package com.card.wiki.moyashi.wikicards

import com.card.wiki.moyashi.wikicards.parameter.ItemData
import java.util.*

interface RxCallbacks {
    fun getTitleCompleted(idListSize: Int)
    fun getArticleCompleted(cardList: ArrayList<ItemData>)
}
