package com.card.wiki.moyashi.wikicards.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.card.wiki.moyashi.wikicards.R
import com.card.wiki.moyashi.wikicards.RxCallbacks
import com.card.wiki.moyashi.wikicards.adapter.CardsAdapter
import com.card.wiki.moyashi.wikicards.http.CustomTabs
import com.card.wiki.moyashi.wikicards.http.ItemData
import com.card.wiki.moyashi.wikicards.http.RxAndroid
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import java.util.*

@Suppress("CAST_NEVER_SUCCEEDS")
class MainActivity : AppCompatActivity(), RxCallbacks, SwipeFlingAdapterView.onFlingListener {
    lateinit var rx: RxAndroid
    private var idList: ArrayList<String>? = null
    private var cardsAdapter: CardsAdapter? = null
    private val itemList = ArrayList<ItemData>()
    private var title: String = ""
    private var count = 0

    private fun onHttpConnect(id: String) {
        rx = RxAndroid()
        rx.setCallback(this)
        rx.onHttpConnect(id)
    }

    private fun SwipeAdapterSettings(itemData: ItemData) {
        itemList.add(itemData)
        if (cardsAdapter != null) {
            cardsAdapter?.notifyDataSetChanged()
            this.title = itemList.get(0).titleText
        } else {
            val flingContainer = findViewById(R.id.swipeAdapter) as SwipeFlingAdapterView
            cardsAdapter = CardsAdapter(itemList, this)
            flingContainer.adapter = cardsAdapter
            flingContainer.setFlingListener(this)
            /** クリック処理 **/
            flingContainer.setOnItemClickListener { itemPosition, dataObject ->
                Log.d(TAG, "click")
//                val customTabs = CustomTabs(this, title)
//                customTabs.onWarmUp()
//                customTabs.onStartUp()
            }
            count++
            onHttpConnect(idList?.get(count) as String)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** LicenseActivityに遷移 **/
        //        val intent = Intent(this, LicenseActivity::class.java)
//        startActivity(intent)

        onHttpConnect(TITLE)
    }

    override fun getTitleCompleted(idList: ArrayList<String>) {
        this.idList = idList
        idList.forEach { Log.d(TAG, it) }
        onHttpConnect(idList.get(count))
    }

    override fun getArticleCompleted(itemData: ItemData) {
        Log.d(TAG, "${itemData.titleText}: ${itemData.articleText}")
        SwipeAdapterSettings(itemData)
    }

    override fun onRightCardExit(p0: Any?) {
        val customTabs = CustomTabs(this, title)
        customTabs.onWarmUp()
        customTabs.onStartUp()
    }

    override fun onLeftCardExit(p0: Any?) {

    }

    override fun onScroll(p0: Float) {

    }

    override fun onAdapterAboutToEmpty(p0: Int) {

    }

    override fun removeFirstObjectInAdapter() {
        itemList.removeAt(0)
        count++
        Log.d(TAG, count.toString())
        if (count + 1 == idList?.size) {
            count = 0
            onHttpConnect(TITLE)
            Log.d(TAG, "if")
        } else {
            onHttpConnect(idList?.get(count) as String)
            Log.d(TAG, "else")
        }
    }

    companion object {
        private val TAG = "MainActivity"
        private val TITLE = "title"
    }
}
