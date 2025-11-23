package com.example.cospicker.community

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.community.adapter.CommentAdapter
import com.example.cospicker.community.model.Comment
import com.example.cospicker.community.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

/**
 * 게시글 상세 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 게시글 정보 표시
 *  - 게시글 좋아요 실시간 반영
 *  - 댓글/대댓글 실시간 표시
 *  - 댓글 추가 / 수정 / 삭제
 *  - 대댓글 추가
 *  - 게시글 수정/삭제
 *  - 알림 기능
 */
class CommunityPostDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid

    private lateinit var adapter: CommentAdapter
    private lateinit var commentList: MutableList<Comment>

    private var isLiked = false
    private var likeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community_post_detail)

        val post = intent.getSerializableExtra("postData") as? Post ?: return

        setupUI(post)
        observePostLike(post)
        setupLikeButton(post)
        setupComments(post)
        setupCommentInput(post)
    }

    /* ---------------------------------------------------------
     * 📌 게시글 UI 기본 구성
     * --------------------------------------------------------- */
    private fun setupUI(post: Post) {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        btnMenu.setOnClickListener { showPopupMenu(btnMenu, post) }

        findViewById<TextView>(R.id.txtCategory).text = post.category
        findViewById<TextView>(R.id.txtNickname).text = post.nickname
        findViewById<TextView>(R.id.txtTime).text = post.time
        findViewById<TextView>(R.id.txtTitle).text = post.title
        findViewById<TextView>(R.id.txtContent).text = post.content
        findViewById<ImageView>(R.id.imgProfile).setImageResource(post.profileImage)
    }

    /* ---------------------------------------------------------
     * 📌 게시글 좋아요 상태 실시간 반영
     * --------------------------------------------------------- */
    private fun observePostLike(post: Post) {
        db.collection("posts").document(post.postId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null || !snapshot.exists()) return@addSnapshotListener

                val updatedLike = snapshot.getLong("likeCount")?.toInt() ?: 0
                val likedUsers = snapshot.get("likedUsers") as? List<String> ?: listOf()

                likeCount = updatedLike
                isLiked = uid != null && likedUsers.contains(uid)

                findViewById<TextView>(R.id.txtLike).text = "$likeCount"
                findViewById<ImageView>(R.id.btnLike).setImageResource(
                    if (isLiked) R.drawable.heart_filled_icon else R.drawable.heart_icon
                )
            }
    }

    /* ---------------------------------------------------------
     * 📌 게시글 좋아요 버튼 처리
     * --------------------------------------------------------- */
    private fun setupLikeButton(post: Post) {
        val btnLike = findViewById<ImageView>(R.id.btnLike)
        val txtLike = findViewById<TextView>(R.id.txtLike)

        btnLike.setOnClickListener {
            if (uid == null) return@setOnClickListener

            isLiked = !isLiked
            likeCount += if (isLiked) 1 else -1

            txtLike.text = "$likeCount"
            btnLike.setImageResource(
                if (isLiked) R.drawable.heart_filled_icon else R.drawable.heart_icon
            )

            updatePostLike(post.postId, isLiked)
        }
    }

    private fun updatePostLike(postId: String, liked: Boolean) {
        val u = uid ?: return

        val updateMap = mutableMapOf<String, Any>(
            "likeCount" to FieldValue.increment(if (liked) 1 else -1),
            "likedUsers" to if (liked)
                FieldValue.arrayUnion(u)
            else
                FieldValue.arrayRemove(u)
        )

        db.collection("posts").document(postId).update(updateMap)
    }

    /* ---------------------------------------------------------
     * 📌 댓글 리스트 UI 구성
     * --------------------------------------------------------- */
    private fun setupComments(post: Post) {
        val recycler = findViewById<RecyclerView>(R.id.recyclerComment)
        commentList = mutableListOf()

        adapter = CommentAdapter(
            commentList,
            onDeleteComment = { commentId -> deleteComment(post.postId, commentId) },
            onEditComment = { comment -> showEditCommentDialog(post.postId, comment) },
            onReplyComment = { comment -> showReplyDialog(post.postId, comment) }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        loadComments(post.postId)
    }

    /* ---------------------------------------------------------
     * 📌 댓글 실시간 불러오기
     * --------------------------------------------------------- */
    private fun loadComments(postId: String) {

        db.collection("posts").document(postId)
            .collection("comments")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener

                commentList.clear()

                for (doc in snapshot.documents) {
                    val comment = doc.toObject(Comment::class.java) ?: continue
                    comment.commentId = doc.id
                    commentList.add(comment)
                }

                adapter.notifyDataSetChanged()
                findViewById<TextView>(R.id.txtCommentCount).text =
                    "댓글 ${commentList.size}"
            }
    }

    /* ---------------------------------------------------------
     * 📌 댓글 입력
     * --------------------------------------------------------- */
    private fun setupCommentInput(post: Post) {
        val editComment = findViewById<EditText>(R.id.editComment)
        val btnSend = findViewById<TextView>(R.id.btnSendComment)

        btnSend.setOnClickListener {
            val text = editComment.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            addComment(post.postId, text)
            editComment.setText("")
        }
    }

    private fun addComment(postId: String, content: String) {
        addCommentToFirestore(postId, parentId = null, content)
    }

    private fun addReply(postId: String, parentId: String, content: String) {
        addCommentToFirestore(postId, parentId, content)
    }

    /* ---------------------------------------------------------
     * 📌 댓글 / 대댓글 Firestore 저장 + 알림 처리
     * --------------------------------------------------------- */
    private fun addCommentToFirestore(postId: String, parentId: String?, content: String) {

        db.collection("users").document(uid ?: return)
            .get()
            .addOnSuccessListener { userDoc ->

                val nickname = userDoc.getString("nickname") ?: "익명"

                val data = hashMapOf(
                    "postId" to postId,
                    "userId" to uid,
                    "nickname" to nickname,
                    "content" to content,
                    "time" to System.currentTimeMillis(),
                    "parentId" to parentId,
                    "likeCount" to 0,
                    "likedUsers" to emptyList<String>()
                )

                db.collection("posts").document(postId)
                    .collection("comments")
                    .add(data)
                    .addOnSuccessListener {

                        // ⭐ 댓글 → 게시글 주인에게 알림
                        if (parentId == null) {
                            sendNotificationToPostOwner(postId, nickname, content)
                        }
                        // ⭐ 대댓글 → 원 댓글 작성자에게 알림
                        else {
                            sendReplyNotification(postId, parentId, nickname, content)
                        }
                    }
            }
    }

    /* ---------------------------------------------------------
     * 📌 댓글 삭제
     * --------------------------------------------------------- */
    private fun deleteComment(postId: String, commentId: String) {
        db.collection("posts").document(postId)
            .collection("comments").document(commentId)
            .delete()
            .addOnSuccessListener {
                adapter.removeCommentById(commentId)
                Toast.makeText(this, "댓글 삭제 완료!", Toast.LENGTH_SHORT).show()
            }
    }

    /* ---------------------------------------------------------
     * 📌 댓글 수정 (Dialog)
     * --------------------------------------------------------- */
    private fun showEditCommentDialog(postId: String, comment: Comment) {

        val dialog = LayoutInflater.from(this)
            .inflate(R.layout.comment_edit_dialog, null)

        val edit = dialog.findViewById<EditText>(R.id.editCommentUpdate)
        edit.setText(comment.content)

        AlertDialog.Builder(this)
            .setTitle("댓글 수정")
            .setView(dialog)
            .setPositiveButton("저장") { _, _ ->
                val newText = edit.text.toString().trim()
                if (newText.isNotEmpty()) {
                    updateComment(postId, comment.commentId, newText)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun updateComment(postId: String, commentId: String, newContent: String) {
        db.collection("posts").document(postId)
            .collection("comments").document(commentId)
            .update("content", newContent)
            .addOnSuccessListener {
                adapter.updateComment(commentId, newContent)
                Toast.makeText(this, "댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    /* ---------------------------------------------------------
     * 📌 대댓글 Dialog
     * --------------------------------------------------------- */
    private fun showReplyDialog(postId: String, parentComment: Comment) {

        val dialog = LayoutInflater.from(this)
            .inflate(R.layout.comment_edit_dialog, null)

        val edit = dialog.findViewById<EditText>(R.id.editCommentUpdate)
        edit.hint = "${parentComment.nickname}님에게 답글"

        AlertDialog.Builder(this)
            .setTitle("답글 작성")
            .setView(dialog)
            .setPositiveButton("등록") { _, _ ->
                val text = edit.text.toString().trim()
                if (text.isNotEmpty()) addReply(postId, parentComment.commentId, text)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    /* ---------------------------------------------------------
     * 📌 게시글 메뉴(수정/삭제)
     * --------------------------------------------------------- */
    private fun showPopupMenu(anchor: ImageView, post: Post) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.community_menu_post_detail, popup.menu)

        // 본인 게시글이 아니면 수정/삭제 숨김
        if (uid != post.userId) {
            popup.menu.findItem(R.id.menu_edit).isVisible = false
            popup.menu.findItem(R.id.menu_delete).isVisible = false
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.menu_edit -> {
                    Toast.makeText(this, "게시글 수정 준비중!", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.menu_delete -> {
                    deletePost(post.postId)
                    true
                }

                else -> false
            }
        }

        popup.show()
    }

    private fun deletePost(postId: String) {
        db.collection("posts").document(postId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "게시글 삭제 완료!", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    /* ---------------------------------------------------------
     * ⭐ 알림: 댓글 → 게시글 작성자에게 전송
     * --------------------------------------------------------- */
    private fun sendNotificationToPostOwner(
        postId: String,
        fromUserName: String,
        content: String
    ) {
        val currentUser = uid ?: return

        db.collection("posts").document(postId).get()
            .addOnSuccessListener { postDoc ->

                val postOwnerId = postDoc.getString("userId") ?: return@addOnSuccessListener

                if (postOwnerId == currentUser) return@addOnSuccessListener

                val data = hashMapOf(
                    "type" to "comment",
                    "postId" to postId,
                    "fromUserId" to currentUser,
                    "fromUserName" to fromUserName,
                    "message" to "내 글에 새로운 댓글: $content",
                    "time" to System.currentTimeMillis(),
                    "read" to false
                )

                db.collection("notifications")
                    .document(postOwnerId)
                    .collection("user_notifications")
                    .add(data)
            }
    }

    /* ---------------------------------------------------------
     * ⭐ 알림: 대댓글 → 원댓글 작성자에게 전송
     * --------------------------------------------------------- */
    private fun sendReplyNotification(
        postId: String,
        parentCommentId: String,
        fromUserName: String,
        content: String
    ) {
        val currentUser = uid ?: return

        db.collection("posts").document(postId)
            .collection("comments").document(parentCommentId)
            .get()
            .addOnSuccessListener { doc ->

                val commentOwnerId = doc.getString("userId") ?: return@addOnSuccessListener

                if (commentOwnerId == currentUser) return@addOnSuccessListener

                val data = hashMapOf(
                    "type" to "reply",
                    "postId" to postId,
                    "commentId" to parentCommentId,
                    "fromUserId" to currentUser,
                    "fromUserName" to fromUserName,
                    "message" to "내 댓글에 답글: $content",
                    "time" to System.currentTimeMillis(),
                    "read" to false
                )

                db.collection("notifications")
                    .document(commentOwnerId)
                    .collection("user_notifications")
                    .add(data)
            }
    }
}
