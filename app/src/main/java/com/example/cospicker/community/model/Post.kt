package com.example.cospicker.community.model

import java.io.Serializable

data class Post(
    var postId: String = "",      // 🔥 Firestore 문서 id 나중에 세팅해야 해서 var
    val userId: String = "",
    val nickname: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "",
    val profileImage: Int = 0,
    val time: String = "",        // "2025-11-17 15:30" 이런 문자열

    var likeCount: Int = 0,       // ❤️ 좋아요 개수
    var isLiked: Boolean = false  // ❤️ 내가 좋아요 눌렀는지 여부 (UI 전용)
) : Serializable
