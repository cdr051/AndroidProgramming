package com.example.cospicker.myinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.myinfo.model.NotificationItem

class NotificationAdapter(
    private val list: List<NotificationItem>,
    private val onClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotiViewHolder>() {

    inner class NotiViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvMessage: TextView = v.findViewById(R.id.tvMessage)
        val tvTime: TextView = v.findViewById(R.id.tvTime)
        val viewDot: View = v.findViewById(R.id.viewDot)

        fun bind(item: NotificationItem) {
            tvMessage.text = item.message
            tvTime.text = convertTime(item.time)

            // 읽음/안읽음 표시
            val dotBg = if (item.read) R.drawable.notify_dot_read
            else R.drawable.notify_dot_unread
            viewDot.setBackgroundResource(dotBg)

            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotiViewHolder(v)
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    private fun convertTime(time: Long): String {
        if (time == 0L) return ""
        val diff = System.currentTimeMillis() - time
        val min = diff / 1000 / 60

        return when {
            min < 1 -> "방금 전"
            min < 60 -> "${min}분 전"
            min < 60 * 24 -> "${min / 60}시간 전"
            else -> "${min / 60 / 24}일 전"
        }
    }
}
