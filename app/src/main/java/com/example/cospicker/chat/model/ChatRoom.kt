package com.example.cospicker.chat.model

data class ChatRoom(
    val chatRoomId: String = "",
    val members: List<String> = emptyList(),
    val otherUserId: String = "",
    val otherUserName: String = "",
    val otherUserProfile: String = "",
    val lastMessage: String = "",
    val lastTime: Long = 0L
)
