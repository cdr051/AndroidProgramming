package com.example.cospicker.myinfo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.community.CommunityPostDetailActivity
import com.example.cospicker.community.adapter.PostAdapter
import com.example.cospicker.community.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPostsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val myPostList = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_my_posts)

        // 상단 제목 세팅
        findViewById<TextView>(R.id.tvTitle).text = "내 글"

        // 뒤로가기
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerMyPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PostAdapter(myPostList) { selectedPost ->
            // 게시글 클릭 → 상세 화면으로 이동
            val intent = Intent(this, CommunityPostDetailActivity::class.java)
            intent.putExtra("postData", selectedPost)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Firestore에서 내 글만 가져오기
        loadMyPosts()
    }

    private fun loadMyPosts() {
        val currentUid = uid ?: return

        db.collection("posts")
            .whereEqualTo("userId", currentUid)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                myPostList.clear()

                for (doc in snapshot.documents) {
                    val post = doc.toObject(Post::class.java) ?: continue
                    post.postId = doc.id
                    myPostList.add(post)
                }

                // 시간 기준 정렬 (time이 문자열이라 대충 최신순 정렬용)
                myPostList.sortByDescending { it.time }

                adapter.notifyDataSetChanged()

                // 상단에 개수 표시 (선택사항)
                val tvCount = findViewById<TextView>(R.id.tvCount)
                tvCount.text = "총 ${myPostList.size}개"
            }
    }
}
