package com.example.cospicker.notice.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cospicker.notice.model.Notice
import com.example.cospicker.R

class NoticeAdapter(
    private val context: Context,
    private val list: List<Notice>
) : ArrayAdapter<Notice>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_notice, parent, false)

        val title = view.findViewById<TextView>(R.id.notice_title)
        val date = view.findViewById<TextView>(R.id.notice_date)

        val item = list[position]

        title.text = item.title
        date.text = item.date

        return view
    }
}
