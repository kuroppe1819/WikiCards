package com.card.wiki.moyashi.wikicards.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.card.wiki.moyashi.wikicards.R
import com.card.wiki.moyashi.wikicards.RxCallbacks
import com.card.wiki.moyashi.wikicards.adapter.CardsAdapter
import com.card.wiki.moyashi.wikicards.http.CustomTabs
import com.card.wiki.moyashi.wikicards.http.ItemData
import com.card.wiki.moyashi.wikicards.http.RxAndroid
import com.card.wiki.moyashi.wikicards.preference.Preferences
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import java.util.*

@Suppress("CAST_NEVER_SUCCEEDS")
class MainActivity : AppCompatActivity(), RxCallbacks, SwipeFlingAdapterView.onFlingListener {
    lateinit private var holder : viewHolder
    lateinit var rx: RxAndroid
    lateinit var preferense : Preferences
    private var idList: ArrayList<String>? = null
    private var cardsAdapter: CardsAdapter? = null
    private val itemList = ArrayList<ItemData>()
    private var title: String = ""
    private var pageCount: Long = 0

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
                val customTabs = CustomTabs(this, title)
                customTabs.onWarmUp()
                customTabs.onStartUp()
            }
            idList?.removeAt(0)
            onHttpConnect(idList?.first() as String)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Countの呼び出し **/
        preferense = Preferences(this)
        pageCount = preferense.onGetCount()
        Log.d(TAG, pageCount.toString())

        /** textViewにCountをセット **/
        holder = viewHolder()
        holder.pageCount = findViewById(R.id.count_textview) as TextView
        holder.pageCount?.setText(pageCount.toString())

        /** LicenseActivityに遷移 **/
        //        val intent = Intent(this, LicenseActivity::class.java)
//        startActivity(intent)

        onHttpConnect(TITLE)
    }

    override fun getTitleCompleted(idList: ArrayList<String>) {
        this.idList = idList
        idList.forEach { Log.d(TAG, it) }
        onHttpConnect(idList.first())
    }

    override fun getArticleCompleted(itemData: ItemData) {
        Log.d(TAG, "${itemData.titleText}: ${itemData.articleText}")
        SwipeAdapterSettings(itemData)
    }

    override fun onRightCardExit(p0: Any?) {

    }

    override fun onLeftCardExit(p0: Any?) {

    }

    override fun onScroll(p0: Float) {

    }

    override fun onAdapterAboutToEmpty(p0: Int) {

    }

    override fun removeFirstObjectInAdapter() {
        pageCount++
        holder.pageCount?.setText(pageCount.toString())
        Log.d(TAG, pageCount.toString())
        itemList.removeAt(0)
        idList?.removeAt(0)
        if (idList?.size == 0) {
            onHttpConnect(TITLE)
            Log.d(TAG, "if")
        } else {
            onHttpConnect(idList?.first() as String)
            Log.d(TAG, "else")
        }
    }

    override fun onPause() {
        super.onPause()
        preferense.onSaveCount(pageCount)
    }

    companion object {
        private val TAG = "MainActivity"
        private val TITLE = "title"

        private class viewHolder() {
            var pageCount: TextView? = null
        }
    }
}
