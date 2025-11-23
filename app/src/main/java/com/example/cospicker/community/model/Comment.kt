package com.example.cospicker.community.model

import java.io.Serializable

data class Comment(
    var commentId: String = "",     // Firestore 문서 id
    val postId: String = "",
    val userId: String = "",
    val nickname: String = "",
    var content: String = "",
    val time: Long = 0L,            // System.currentTimeMillis()

    val parentId: String? = null,   // ⭐ 대댓글이면 부모 댓글 id, 일반 댓글이면 null
    var likeCount: Int = 0,         // ⭐ 댓글 좋아요 수
    var likedUsers: List<String> = emptyList() // ⭐ 좋아요 누른 사용자 id 리스트
) : Serializable
