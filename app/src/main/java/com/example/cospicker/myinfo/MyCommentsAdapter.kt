package com.example.cospicker.myinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.community.model.Comment

class MyCommentsAdapter(
    private val list: List<Pair<Comment, String>>, // (댓글, postId)
    private val onClick: (Comment, String) -> Unit
) : RecyclerView.Adapter<MyCommentsAdapter.MyViewHolder>() {

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvContent: TextView = v.findViewById(R.id.tvMyCommentContent)
        val tvTime: TextView = v.findViewById(R.id.tvMyCommentTime)

        fun bind(item: Pair<Comment, String>) {
            val (comment, postId) = item

            tvContent.text = comment.content
            tvTime.text = convertTime(comment.time)

            itemView.setOnClickListener {
                onClick(comment, postId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_comment, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    private fun convertTime(time: Long): String {
        val diff = System.currentTimeMillis() - time
        val min = diff / 60000

        return when {
            min < 1 -> "방금 전"
            min < 60 -> "${min}분 전"
            min < 60 * 24 -> "${min / 60}시간 전"
            else -> "${min / 60 / 24}일 전"
        }
    }
}
