package com.example.cospicker.community

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.chat.ChatListActivity
import com.example.cospicker.community.adapter.PostAdapter
import com.example.cospicker.community.model.Post
import com.example.cospicker.home.HomeActivity
import com.example.cospicker.myinfo.ProfileActivity
import com.example.cospicker.auth.LoginIntroActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 커뮤니티 메인 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 게시글 목록 표시
 *  - 최신순 정렬
 *  - 검색창 → CommunitySearchActivity 이동
 *  - 글쓰기 → CommunityWritePostActivity 이동
 *  - 게시글 클릭 → 상세 화면 이동
 *  - 하단 네비게이션 처리
 */
class CommunityActivity : AppCompatActivity() {

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val uid: String? get() = FirebaseAuth.getInstance().uid

    // RecyclerView
    private lateinit var recyclerPost: RecyclerView
    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community_main)

        /* ---------------------------------------------------------
         * 🔍 검색창 (검색창·에디트텍스트 모두 검색 페이지 이동)
         * --------------------------------------------------------- */
        val searchBox = findViewById<LinearLayout>(R.id.search_box)
        val editSearch = findViewById<EditText>(R.id.edit_search)

        // 검색창 입력 비활성 → 클릭만 가능하게
        editSearch.apply {
            isFocusable = false
            isClickable = true
        }

        searchBox.setOnClickListener {
            startActivity(Intent(this, CommunitySearchActivity::class.java))
        }
        editSearch.setOnClickListener {
            startActivity(Intent(this, CommunitySearchActivity::class.java))
        }

        /* ---------------------------------------------------------
         * ✏ 글쓰기 버튼
         * --------------------------------------------------------- */
        findViewById<TextView>(R.id.btn_write).setOnClickListener {
            startActivity(Intent(this, CommunityWritePostActivity::class.java))
        }

        /* ---------------------------------------------------------
         * ⭐ RecyclerView 설정
         * --------------------------------------------------------- */
        recyclerPost = findViewById(R.id.recycler_post)
        recyclerPost.layoutManager = LinearLayoutManager(this)

        adapter = PostAdapter(postList) { selectedPost ->
            val intent = Intent(this, CommunityPostDetailActivity::class.java)
            intent.putExtra("postData", selectedPost)
            startActivity(intent)
        }
        recyclerPost.adapter = adapter

        /* ---------------------------------------------------------
         * 🔥 Firestore에서 게시글 로드 (실시간 업데이트)
         * --------------------------------------------------------- */
        loadPosts()

        /* ---------------------------------------------------------
         * ⭐⭐ 하단 네비게이션 처리 ⭐⭐
         * --------------------------------------------------------- */

        // 🏠 홈
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        // 💬 메시지
        findViewById<LinearLayout>(R.id.nav_message).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        // 👤 프로필 (로그인 여부에 따라 다른 화면)
        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            val prefs = getSharedPreferences("user", MODE_PRIVATE)
            val isLogin = prefs.getBoolean("isLogin", false)

            if (isLogin) {
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this, LoginIntroActivity::class.java))
            }
        }
    }

    /**
     * 게시글 목록 Firestore에서 실시간 로드
     * ---------------------------------------------------------
     *  - likedUsers 기반으로 내 좋아요 여부 계산
     *  - 최신순 정렬
     *  - UI 새로고침
     */
    private fun loadPosts() {
        db.collection("posts")
            .addSnapshotListener { snapshot, e ->
                if (snapshot == null || e != null) return@addSnapshotListener

                postList.clear()

                for (doc in snapshot.documents) {
                    val post = doc.toObject(Post::class.java) ?: continue

                    // Firestore 문서 ID → postId로 저장
                    post.postId = doc.id

                    // 내 좋아요 여부 확인
                    val likedUsers = doc.get("likedUsers") as? List<String> ?: emptyList()
                    post.isLiked = uid != null && likedUsers.contains(uid)

                    postList.add(post)
                }

                // 최신순 정렬
                postList.sortByDescending { it.time }

                adapter.notifyDataSetChanged()
            }
    }
}
