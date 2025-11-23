package com.example.cospicker.community.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.community.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 커뮤니티 게시글 목록 Adapter
 * ----------------------------------------------------
 * 기능:
 *  - 게시글 기본 정보 표시(닉네임, 카테고리, 시간, 내용, 좋아요)
 *  - 게시글 클릭 시 상세 화면 이동
 *  - 게시글 좋아요(로컬 즉시 반영 + Firestore 동기화)
 */
class PostAdapter(
    private val postList: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val uid: String? get() = FirebaseAuth.getInstance().uid

    /**
     * ViewHolder: item_post.xml과 연결되는 UI 요소들 보관
     */
    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgProfile: ImageView = itemView.findViewById(R.id.img_profile)
        val tvNickname: TextView = itemView.findViewById(R.id.tv_nickname)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvContent: TextView = itemView.findViewById(R.id.tv_content)

        val tvLike: TextView = itemView.findViewById(R.id.tv_like)
        val imgLike: ImageView = itemView.findViewById(R.id.img_like)

        init {
            // 🔘 게시글 클릭 → 상세 화면 이동
            itemView.setOnClickListener {
                onItemClick(postList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    /**
     * 게시글 UI 바인딩
     */
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        // 🖼 프로필 이미지
        holder.imgProfile.setImageResource(post.profileImage)

        // 📝 게시글 정보
        holder.tvNickname.text = post.nickname
        holder.tvCategory.text = post.category
        holder.tvTime.text = post.time
        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content

        // ❤️ 좋아요 갯수
        holder.tvLike.text = post.likeCount.toString()

        // 좋아요 UI 반영
        holder.imgLike.setImageResource(
            if (post.isLiked) R.drawable.heart_filled_icon
            else R.drawable.heart_icon
        )

        /* ---------------------------------------------------------
         * ❤️ 좋아요 버튼 클릭 처리
         * --------------------------------------------------------- */
        holder.imgLike.setOnClickListener {
            val currentUid = uid ?: return@setOnClickListener
            if (post.postId.isEmpty()) return@setOnClickListener

            // 1) 로컬 상태 변경
            post.isLiked = !post.isLiked

            // 2) 로컬 Like 카운트 변경 → 즉시 UI 반영
            if (post.isLiked) {
                post.likeCount++
                holder.imgLike.setImageResource(R.drawable.heart_filled_icon)
            } else {
                post.likeCount--
                holder.imgLike.setImageResource(R.drawable.heart_icon)
            }

            holder.tvLike.text = post.likeCount.toString()

            // 3) Firestore 동기화
            updateLikeInFirestore(
                postId = post.postId,
                uid = currentUid,
                diff = if (post.isLiked) +1 else -1,
                liked = post.isLiked
            )
        }
    }

    override fun getItemCount(): Int = postList.size

    /**
     * Firestore 좋아요 정보 업데이트
     * ----------------------------------------------------
     * - likeCount 증가/감소(FieldValue.increment)
     * - likedUsers 배열 add/remove
     */
    private fun updateLikeInFirestore(postId: String, uid: String, diff: Int, liked: Boolean) {

        val updateMap = mutableMapOf<String, Any>(
            "likeCount" to FieldValue.increment(diff.toLong())
        )

        updateMap["likedUsers"] = if (liked)
            FieldValue.arrayUnion(uid)
        else
            FieldValue.arrayRemove(uid)

        db.collection("posts").document(postId)
            .update(updateMap)
            .addOnFailureListener { it.printStackTrace() }
    }
}
