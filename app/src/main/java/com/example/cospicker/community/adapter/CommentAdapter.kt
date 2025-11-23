package com.example.cospicker.community.adapter

import android.view.*
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.community.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/**
 * 댓글 RecyclerView Adapter
 * ----------------------------------------------------
 * 기능:
 *  - 댓글/대댓글 표시
 *  - 댓글 좋아요
 *  - 댓글 수정/삭제
 *  - 대댓글 작성 콜백 전달
 *
 * 구조:
 * 1) 댓글 기본 UI 바인딩
 * 2) 대댓글 여부에 따라 좌측 padding 조절
 * 3) 본인 댓글 → 수정/삭제 가능
 * 4) 좋아요 처리 (로컬 + Firestore 동기화)
 * 5) 길게 눌러 팝업 메뉴 노출
 */
class CommentAdapter(
    private val commentList: MutableList<Comment>,
    private val onDeleteComment: (String) -> Unit,
    private val onEditComment: (Comment) -> Unit,
    private val onReplyComment: (Comment) -> Unit        // ⭐ 대댓글 콜백
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val uid = FirebaseAuth.getInstance().uid
    private val db = FirebaseFirestore.getInstance()

    /**
     * ViewHolder : item_comment.xml의 요소들 바인딩
     */
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val tvNickname: TextView = itemView.findViewById(R.id.tvNickname)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvReply: TextView = itemView.findViewById(R.id.tvReply)

        val btnLike: ImageView = itemView.findViewById(R.id.btnLikeComment)
        val txtLikeCount: TextView = itemView.findViewById(R.id.txtLikeCount)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(v)
    }

    /**
     * 댓글 데이터 바인딩
     */
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]

        // 닉네임 / 내용 / 시간
        holder.tvNickname.text = comment.nickname
        holder.tvContent.text = comment.content
        holder.tvTime.text = formatTime(comment.time)

        /* ---------------------------------------------------------
         * 🧩 대댓글 UI (들여쓰기)
         *  - parentId가 존재하면 = 대댓글
         * --------------------------------------------------------- */
        if (comment.parentId != null) {
            holder.itemView.setPadding(
                60,
                holder.itemView.paddingTop,
                holder.itemView.paddingRight,
                holder.itemView.paddingBottom
            )
        } else {
            holder.itemView.setPadding(
                16,
                holder.itemView.paddingTop,
                holder.itemView.paddingRight,
                holder.itemView.paddingBottom
            )
        }

        /* ---------------------------------------------------------
         * 🗑 본인 댓글이면 삭제 버튼 보이기
         * --------------------------------------------------------- */
        holder.btnDelete.visibility =
            if (comment.userId == uid) View.VISIBLE else View.GONE

        holder.btnDelete.setOnClickListener {
            onDeleteComment(comment.commentId)
        }

        /* ---------------------------------------------------------
         * 💬 대댓글 버튼 클릭
         * --------------------------------------------------------- */
        holder.tvReply.setOnClickListener {
            onReplyComment(comment)
        }

        /* ---------------------------------------------------------
         * ❤️ 댓글 좋아요 처리
         * --------------------------------------------------------- */
        val isLiked = uid != null && comment.likedUsers.contains(uid)

        // 좋아요 아이콘 상태 설정
        holder.btnLike.setImageResource(
            if (isLiked) R.drawable.heart_filled_icon
            else R.drawable.heart_icon
        )
        holder.txtLikeCount.text = comment.likeCount.toString()

        holder.btnLike.setOnClickListener {
            val currentUid = uid ?: return@setOnClickListener
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            val c = commentList[pos]
            val nowLike = !c.likedUsers.contains(currentUid)
            val diff = if (nowLike) 1 else -1

            // 🔹 로컬 즉시 반영(UX 빠르게 처리)
            c.likeCount += diff
            c.likedUsers = if (nowLike)
                c.likedUsers + currentUid
            else
                c.likedUsers.filter { it != currentUid }

            notifyItemChanged(pos)

            // 🔹 Firestore 반영
            db.collection("posts").document(c.postId)
                .collection("comments").document(c.commentId)
                .update(
                    mapOf(
                        "likeCount" to FieldValue.increment(diff.toLong()),
                        "likedUsers" to if (nowLike)
                            FieldValue.arrayUnion(currentUid)
                        else
                            FieldValue.arrayRemove(currentUid)
                    )
                )
        }

        /* ---------------------------------------------------------
         * 📌 길게 누르면 수정/삭제 팝업 (본인 댓글만)
         * --------------------------------------------------------- */
        holder.itemView.setOnLongClickListener {
            if (comment.userId == uid) {
                showPopup(holder, comment)
            }
            true
        }
    }

    override fun getItemCount() = commentList.size

    /**
     * 수정/삭제 팝업 메뉴
     */
    private fun showPopup(holder: CommentViewHolder, comment: Comment) {
        val popup = PopupMenu(holder.itemView.context, holder.itemView)
        popup.menu.add("수정")
        popup.menu.add("삭제")

        popup.setOnMenuItemClickListener {
            when (it.title) {
                "수정" -> onEditComment(comment)
                "삭제" -> onDeleteComment(comment.commentId)
            }
            true
        }
        popup.show()
    }

    /**
     * 댓글 내용 업데이트 (수정 시)
     */
    fun updateComment(commentId: String, newContent: String) {
        val index = commentList.indexOfFirst { it.commentId == commentId }
        if (index != -1) {
            commentList[index].content = newContent
            notifyItemChanged(index)
        }
    }

    /**
     * 댓글 삭제
     */
    fun removeCommentById(commentId: String) {
        val index = commentList.indexOfFirst { it.commentId == commentId }
        if (index != -1) {
            commentList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    /**
     * 시간 포맷 변환
     * ----------------------------------------------------
     * 예: "방금 전", "5분 전", "3시간 전", "2일 전"
     * 1주 이상 → yyyy-MM-dd 로 표시
     */
    private fun formatTime(millis: Long): String {
        if (millis == 0L) return ""
        val diff = System.currentTimeMillis() - millis
        val minutes = diff / 60000
        val hours = diff / 3600000
        val days = diff / (24 * 3600000)

        return when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days < 7 -> "${days}일 전"
            else -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
        }
    }
}
