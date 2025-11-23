package com.example.cospicker.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.chat.adapter.ChatListAdapter
import com.example.cospicker.chat.model.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 채팅방 목록 화면
 * ----------------------------------------------------
 * 기능:
 *  - 사용자가 참여한 모든 채팅방 리스트 불러오기
 *  - 최근 메시지 / 상대방 정보 표시
 *  - 채팅방 클릭 → ChatRoomActivity 이동
 */
class ChatListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val chatList = mutableListOf<ChatRoom>()       // 채팅방 목록 저장

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_list)

        /* ---------------------------------------------------------
         * 📌 RecyclerView 설정
         * --------------------------------------------------------- */
        recyclerView = findViewById(R.id.recyclerChatList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 🔥 Firestore에서 채팅방 실시간 로드
        loadChatRooms()
    }

    /**
     * Firebase Firestore에서 채팅방 목록 가져오기
     * - whereArrayContains("members", uid) : 현재 사용자가 속한 방만 조회
     * - SnapshotListener : 채팅 목록 실시간 업데이트
     */
    private fun loadChatRooms() {
        db.collection("chats")
            .whereArrayContains("members", uid)
            .addSnapshotListener { snapshot, e ->

                if (e != null) {
                    Log.e("ChatList", "Error loading chat rooms", e)
                    return@addSnapshotListener
                }

                chatList.clear()

                snapshot?.documents?.forEach { doc ->
                    val room = doc.toObject(ChatRoom::class.java)
                    if (room != null) chatList.add(room)
                }

                /* ---------------------------------------------------------
                 * 🔗 Adapter 연결 + 채팅방 클릭 이벤트 처리
                 * --------------------------------------------------------- */
                recyclerView.adapter =
                    ChatListAdapter(chatList) { room ->
                        val intent = Intent(this, ChatRoomActivity::class.java)
                        intent.putExtra("chatRoomId", room.chatRoomId)
                        intent.putExtra("otherUserId", room.otherUserId)
                        startActivity(intent)
                    }
            }
    }
}
