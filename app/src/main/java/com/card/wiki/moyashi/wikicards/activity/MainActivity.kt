package com.card.wiki.moyashi.wikicards.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
class MainActivity : AppCompatActivity(), RxCallbacks, SwipeFlingAdapterView.onFlingListener, View.OnClickListener {
    lateinit private var holder: viewHolder
    lateinit var preferense: Preferences
    lateinit var rx: RxAndroid
    lateinit var customTabs : CustomTabs
    private var cardsAdapter: CardsAdapter? = null
    private var itemList = ArrayList<ItemData>()
    private var title: String = ""
    private var count: Long = 0

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
    
    private fun countCheck(count : Long){
        if (count == 9223372036854775807) this.count = 0
    }

    override fun onClick(p0: View?) {
        /** LicenseActivityに遷移 **/
        val intent = Intent(this, LicenseActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Countの呼び出し **/
        preferense = Preferences(this)
        count = preferense.onGetCount()
        /** textViewにCountをセット **/
        holder = viewHolder()
        holder.count = findViewById(R.id.count_textview) as TextView
        holder.count?.setText(count.toString())
        holder.count?.setOnClickListener(this)

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
        count++
        countCheck(count)
        holder.count?.setText(count.toString())
        itemList.removeAt(0)
        cardsAdapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        preferense.onSaveCount(count)
    }

    companion object {
        private val TAG = "MainActivity"
        private class viewHolder() {
            var count: TextView? = null
        }
    }
}
