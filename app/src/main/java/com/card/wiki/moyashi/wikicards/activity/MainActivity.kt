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

class MainActivity : AppCompatActivity(), RxCallbacks, SwipeFlingAdapterView.onFlingListener {
    private var idList: ArrayList<String>? = null
    private var cardsAdapter: CardsAdapter? = null
    private val arrayList = ArrayList<ItemData>()
    private var title: String = ""
    private var count = 0


    private fun SwipeAdapterSettings(title: String, article: String) {
        val itemData = ItemData()
        itemData.titleText = title
        itemData.articleText = article
        arrayList.add(itemData)
        if (cardsAdapter != null) {
            cardsAdapter?.notifyDataSetChanged()
            this.title = arrayList.get(0).titleText
            Log.d(TAG, "changed")
        } else {
            val flingContainer = findViewById(R.id.swipeAdapter) as SwipeFlingAdapterView
            cardsAdapter = CardsAdapter(arrayList, this)
            flingContainer.adapter = cardsAdapter
            flingContainer.setFlingListener(this)
            count++
            onHttpConnect()
        }
    }
    
    private fun onHttpConnect(){
        RxAndroid(this).onHttpConnect(idList?.get(count) as String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** LicenseActivityに遷移 **/
        //        val intent = Intent(this, LicenseActivity::class.java)
//        startActivity(intent)

        RxAndroid(this).onHttpConnect("title")
    }

    override fun getTitleCompleted(idList: ArrayList<String>) {
        this.idList = idList
        idList.forEach { Log.d(TAG, it) }
        onHttpConnect()
    }

    override fun getArticleCompleted(title: String, article: String) {
        Log.d(TAG, "${title}: ${article}")
        SwipeAdapterSettings(title, article)
    }

    override fun onRightCardExit(p0: Any?) {
        val customTabs = CustomTabs(this, title)
        customTabs.onWarmUp()
        customTabs.onStartUp()
    }

    override fun onLeftCardExit(p0: Any?) {
        onHttpConnect()
    }

    override fun onScroll(p0: Float) {

    }

    override fun onAdapterAboutToEmpty(p0: Int) {

    }

    override fun removeFirstObjectInAdapter() {

    }

    companion object {
        private val TAG = "MainActivity"
    }
}
