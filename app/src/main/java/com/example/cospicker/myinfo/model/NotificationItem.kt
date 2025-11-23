package com.example.cospicker.myinfo.model

data class NotificationItem(
    var notificationId: String = "",
    val type: String = "",          // "comment" / "reply"
    val postId: String = "",
    val commentId: String? = null,
    val fromUserId: String = "",
    val fromUserName: String = "",
    val message: String = "",
    val time: Long = 0L,
    val read: Boolean = false
)
