package com.card.wiki.moyashi.wikicards.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.card.wiki.moyashi.wikicards.R
import com.card.wiki.moyashi.wikicards.RxCallbacks
import com.card.wiki.moyashi.wikicards.adapter.CardsAdapter
import com.card.wiki.moyashi.wikicards.http.CustomTabs
import com.card.wiki.moyashi.wikicards.parameter.ItemData
import com.card.wiki.moyashi.wikicards.http.RxAndroid
import com.card.wiki.moyashi.wikicards.preference.Preferences
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import java.util.*

@Suppress("CAST_NEVER_SUCCEEDS")
class MainActivity : AppCompatActivity(), RxCallbacks, SwipeFlingAdapterView.onFlingListener {
    lateinit private var holder: viewHolder
    lateinit var preferense: Preferences
    lateinit var rx: RxAndroid
    lateinit var customTabs : CustomTabs
    private var cardsAdapter: CardsAdapter? = null
    private var itemList = ArrayList<ItemData>()
    private var title: String = ""
    private var pageCount: Long = 0

    private fun onHttpConnect(type: String) {
        rx = RxAndroid()
        rx.setCallback(this)
        rx.onHttpConnect(type)
    }

    private fun SwipeAdapterSettings() {
        if (cardsAdapter != null) {
            cardsAdapter?.notifyDataSetChanged()
        } else {
            val flingContainer = findViewById(R.id.swipeAdapter) as SwipeFlingAdapterView
            cardsAdapter = CardsAdapter(itemList, this)
            flingContainer.adapter = cardsAdapter
            flingContainer.setFlingListener(this)
            /** クリック処理 **/
            flingContainer.setOnItemClickListener { itemPosition, dataObject ->
                val customTabs = CustomTabs(this, title)
                customTabs.onWarmUp()
                customTabs.onStartUp()
            }
            SwipeAdapterSettings()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Countの呼び出し **/
        preferense = Preferences(this)
        pageCount = preferense.onGetCount()

        /** textViewにCountをセット **/
        holder = viewHolder()
        holder.pageCount = findViewById(R.id.count_textview) as TextView
        holder.pageCount?.setText(pageCount.toString())

        /** LicenseActivityに遷移 **/
        //        val intent = Intent(this, LicenseActivity::class.java)
//        startActivity(intent)

        /** Wikiの概要を取得 **/
        onHttpConnect("title")
    }

    override fun getTitleCompleted() {
        onHttpConnect("article")
    }

    override fun getArticleCompleted(itemList: ArrayList<ItemData>) {
        itemList.forEach{
            Log.d(TAG, it.titleText)
            this.itemList.add(it)
        }
        SwipeAdapterSettings()
    }

    override fun onRightCardExit(p0: Any?) {
        customTabs = CustomTabs(this, title)
        customTabs.onWarmUp()
        customTabs.onStartUp()
    }

    override fun onLeftCardExit(p0: Any?) {

    }

    override fun onScroll(p0: Float) {

    }

    override fun onAdapterAboutToEmpty(stock: Int) {
        Log.d(TAG, "Stock : ${stock}")
        if (stock == 10) {
            onHttpConnect("title")
        }
    }

    override fun removeFirstObjectInAdapter() {
        this.title = itemList.first().titleText
        pageCount++
        holder.pageCount?.setText(pageCount.toString())
        itemList.removeAt(0)
        cardsAdapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        preferense.onSaveCount(pageCount)
        customTabs.unbindCustomTabsService()
    }

    companion object {
        private val TAG = "MainActivity"
        private class viewHolder() {
            var pageCount: TextView? = null
        }
    }
}
