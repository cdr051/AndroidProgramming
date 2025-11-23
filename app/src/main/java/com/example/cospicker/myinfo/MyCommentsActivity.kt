package com.example.cospicker.myinfo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.community.adapter.CommentAdapter
import com.example.cospicker.community.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyCommentsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid
    private lateinit var adapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_my_comments)

        // 🔙 뒤로가기 버튼
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerMyComments)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = CommentAdapter(
            commentList,
            onDeleteComment = {},   // 내 댓글 목록에서는 삭제 기능 사용 X
            onEditComment = {},
            onReplyComment = {}
        )
        recycler.adapter = adapter

        loadMyComments()
    }

    private fun loadMyComments() {
        if (uid == null) return

        db.collectionGroup("comments")   // 모든 posts/*/comments 검색
            .whereEqualTo("userId", uid)
            .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener

                commentList.clear()

                for (doc in snapshot.documents) {
                    val c = doc.toObject(Comment::class.java) ?: continue
                    c.commentId = doc.id
                    commentList.add(c)
                }

                adapter.notifyDataSetChanged()
            }
    }
}
