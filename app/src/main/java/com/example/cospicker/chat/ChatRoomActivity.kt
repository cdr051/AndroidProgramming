package com.example.cospicker.chat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R

/**
 * 개별 채팅방 화면
 * ----------------------------------------------------
 * 기능:
 *  - 전달받은 chatRoomId / otherUserId 확인
 *  - 실제 메시지 로딩 / 전송 UI는 이후 구현 예정
 */
class ChatRoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // activity_chat_room.xml 레이아웃 사용
        setContentView(R.layout.chat_room)

        /* ---------------------------------------------------------
         * 📦 ChatListActivity에서 전달된 값 받기
         * --------------------------------------------------------- */
        val chatRoomId = intent.getStringExtra("chatRoomId")
        val otherUserId = intent.getStringExtra("otherUserId")

        Log.d("ChatRoomTest", "받은 chatRoomId = $chatRoomId")
        Log.d("ChatRoomTest", "받은 otherUserId = $otherUserId")
    }
}
