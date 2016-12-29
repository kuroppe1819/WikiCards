package com.card.wiki.moyashi.wikicards.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.card.wiki.moyashi.wikicards.R
import com.card.wiki.moyashi.wikicards.http.ItemData
import java.util.*

class CardsAdapter(arrayList : ArrayList<ItemData>, context : Context) : BaseAdapter() {
    private var arrayList: ArrayList<ItemData>
    private var context: Context

    init {
        this.arrayList = arrayList
        this.context = context
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val textData = arrayList.get(position)
        var view = convertView
        val holder = viewHolder()
        if (view == null) {
            val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.card_item, parent, false)
            holder.title = view.findViewById(R.id.titleText) as TextView
            holder.article = view.findViewById(R.id.articleText) as TextView
            holder.title?.setText(textData.titleText)
            holder.article?.setText(textData.articleText)
        }
        return view
    }

    companion object {
        val TAG = "CardsAdapter"

        private class viewHolder() {
            var title: TextView? = null
            var article: TextView? = null
        }
    }
}
