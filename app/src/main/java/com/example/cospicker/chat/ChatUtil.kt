package com.example.cospicker.chat

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.cospicker.chat.model.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ChatUtil
 * ----------------------------------------------------
 * 채팅방 처리 핵심 유틸리티
 *
 * 기능:
 * 1. 특정 사용자와 채팅방이 이미 있는지 확인
 * 2. 기존 방이 존재하면 → 해당 방으로 이동
 * 3. 없으면 → 새 채팅방 생성 후 이동
 */
object ChatUtil {

    /**
     * 채팅 시작 로직
     *
     * @param context 화면 이동을 위한 context
     * @param otherUserId 상대방 UID
     * @param otherUserName 상대방 닉네임
     */
    fun startChat(context: Context, otherUserId: String, otherUserName: String) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid ?: return

        /* ---------------------------------------------------------
         * 1️⃣ 현재 유저가 속한 모든 채팅방 가져오기
         *    → 이미 상대방과의 방이 있는지 검사
         * --------------------------------------------------------- */
        db.collection("chats")
            .whereArrayContains("members", uid)
            .get()
            .addOnSuccessListener { rooms ->

                // 🔍 기존 방 찾기
                for (doc in rooms) {
                    val members = doc.get("members") as List<String>

                    // 현재 방 멤버에 상대방이 포함되어 있으면 = 기존 방 존재
                    if (members.contains(otherUserId)) {
                        openChatRoom(context, doc.id, otherUserId)
                        return@addOnSuccessListener
                    }
                }

                // 🔥 기존 방이 없다면 새 방 생성
                createNewChatRoom(context, otherUserId, otherUserName)
            }
    }

    /**
     * 새 채팅방 생성
     */
    private fun createNewChatRoom(context: Context, otherUserId: String, otherUserName: String) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid ?: return

        val newRoomId = db.collection("chats").document().id   // 자동 채팅방 ID 생성

        /* ---------------------------------------------------------
         * 새 채팅방 데이터 모델 생성 (ChatRoom)
         * --------------------------------------------------------- */
        val chatRoomData = ChatRoom(
            chatRoomId = newRoomId,
            members = listOf(uid, otherUserId),
            otherUserId = otherUserId,
            otherUserName = otherUserName,
            lastMessage = "",
            lastTime = System.currentTimeMillis()
        )

        /* ---------------------------------------------------------
         * Firestore에 새 채팅방 저장
         * --------------------------------------------------------- */
        db.collection("chats").document(newRoomId)
            .set(chatRoomData)
            .addOnSuccessListener {
                openChatRoom(context, newRoomId, otherUserId)
            }
            .addOnFailureListener {
                Log.e("ChatUtil", "채팅방 생성 실패", it)
            }
    }

    /**
     * 채팅방 화면으로 이동
     */
    private fun openChatRoom(context: Context, chatRoomId: String, otherUserId: String) {
        val intent = Intent(context, ChatRoomActivity::class.java)
        intent.putExtra("chatRoomId", chatRoomId)
        intent.putExtra("otherUserId", otherUserId)
        context.startActivity(intent)
    }
}
