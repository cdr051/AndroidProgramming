package com.example.cospicker.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.chat.model.ChatRoom

/**
 * 채팅 목록 RecyclerView Adapter
 * ----------------------------------------------------
 * 기능:
 *  - 채팅방 목록 표시
 *  - 최근 메시지 / 시간 / 상대방 이름 표시
 *  - 채팅방 클릭 이벤트 처리(onClick)
 */
class ChatListAdapter(
    private val items: List<ChatRoom>,
    private val onClick: (ChatRoom) -> Unit   // 채팅방 클릭 콜백
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    /**
     * ViewHolder: item_chat_room.xml과 연결되는 UI 요소 보관
     */
    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val img: ImageView = view.findViewById(R.id.imgProfile)
        val name: TextView = view.findViewById(R.id.txtName)
        val lastMessage: TextView = view.findViewById(R.id.txtLastMessage)
        val time: TextView = view.findViewById(R.id.txtTime)

        /**
         * 각 채팅방 데이터를 화면에 바인딩
         */
        fun bind(item: ChatRoom) {
            name.text = item.otherUserName
            lastMessage.text = item.lastMessage
            time.text = formatTime(item.lastTime)

            // 🔘 아이템 클릭 → onClick 콜백 전달
            itemView.setOnClickListener { onClick(item) }
        }
    }

    /**
     * ViewHolder 생성
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_room, parent, false)
        return ChatViewHolder(view)
    }

    /**
     * ViewHolder에 데이터 바인딩
     */
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * 아이템 개수 반환
     */
    override fun getItemCount(): Int = items.size

    /**
     * 시간 표시 포맷 변환
     * ----------------------------------------------------
     * - 1시간 미만 → "방금 전"
     * - 24시간 미만 → "n시간 전"
     * - 그 이상 → "n일 전"
     */
    private fun formatTime(time: Long): String {
        val diff = System.currentTimeMillis() - time
        val hour = 1000 * 60 * 60
        val day = hour * 24

        return when {
            diff < hour -> "방금 전"
            diff < day -> "${diff / hour}시간 전"
            else -> "${diff / day}일 전"
        }
    }
}
