package com.example.cospicker.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.chat.model.Message

/**
 * 채팅 메시지 Adapter
 * ----------------------------------------------------
 * 기능:
 *  - 내가 보낸 메시지 / 상대가 보낸 메시지 구분하여 레이아웃 적용
 *  - 메시지 내용 표시
 *
 * viewType:
 *  - TYPE_ME:     내가 보낸 메시지 (item_message_me.xml)
 *  - TYPE_OTHER:  상대방 메시지 (item_message_other.xml)
 */
class ChatMessageAdapter(
    private val uid: String,                 // 현재 로그인한 사용자 UID
    private val messages: List<Message>      // 전체 메시지 목록
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ME = 1
    private val TYPE_OTHER = 2

    /**
     * 메시지의 발신자에 따라 ViewType 결정
     */
    override fun getItemViewType(position: Int): Int =
        if (messages[position].senderId == uid) TYPE_ME else TYPE_OTHER

    /**
     * ViewHolder 생성
     * - 나 / 상대방에 따라 다른 XML 레이아웃 inflate
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_ME) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_me, parent, false)
            MeViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_other, parent, false)
            OtherViewHolder(view)
        }

    /**
     * ViewHolder에 데이터 바인딩
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is MeViewHolder) holder.bind(message)
        if (holder is OtherViewHolder) holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    /**
     * 🟦 내가 보낸 메시지 ViewHolder
     */
    inner class MeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtMessage: TextView = view.findViewById(R.id.txtMessage)

        fun bind(message: Message) {
            txtMessage.text = message.message
        }
    }

    /**
     * 🟨 상대방 메시지 ViewHolder
     */
    inner class OtherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtMessage: TextView = view.findViewById(R.id.txtMessage)

        fun bind(message: Message) {
            txtMessage.text = message.message
        }
    }
}
