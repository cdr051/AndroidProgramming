package com.example.cospicker.community

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.community.adapter.PostAdapter
import com.example.cospicker.community.model.Post
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 커뮤니티 검색 결과 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 검색 키워드 기반 결과 표시
 *  - 글 유형 / 태그 스피너 필터
 *  - 인기/맛집/숙소 등의 Chip 필터
 *  - 게시글 클릭 → 상세 화면 이동
 */
class CommunitySearchResultActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid: String? get() = FirebaseAuth.getInstance().uid

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()

    private lateinit var types: List<String>
    private lateinit var tags: List<String>

    // 현재 선택된 필터 값
    private var currentKeyword: String = ""
    private var currentType: String = "전체"
    private var currentTag: String = "전체"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community_search_result)

        /* ---------------------------------------------------------
         * 🔙 뒤로가기
         * --------------------------------------------------------- */
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 🔎 전달받은 검색 키워드 표시
         * --------------------------------------------------------- */
        currentKeyword = intent.getStringExtra("keyword") ?: ""
        findViewById<TextView>(R.id.txtKeyword).text = "'$currentKeyword' 검색 결과"

        /* ---------------------------------------------------------
         * 🗂 스피너(글 유형 / 태그)
         * --------------------------------------------------------- */
        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        val spinnerTag = findViewById<Spinner>(R.id.spinnerTag)

        types = listOf("전체", "일반글", "플래너", "숙소", "맛집", "후기")
        tags = listOf("전체", "맛집", "숙소", "카페", "여행", "후기")

        spinnerType.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        spinnerTag.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tags)

        /* ---------------------------------------------------------
         * 📰 RecyclerView 설정
         * --------------------------------------------------------- */
        recyclerView = findViewById(R.id.recyclerSearch)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PostAdapter(postList) { post ->
            val intent = Intent(this, CommunityPostDetailActivity::class.java)
            intent.putExtra("postData", post)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        /* ---------------------------------------------------------
         * 🗂 스피너 선택 이벤트
         * --------------------------------------------------------- */
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?,
                position: Int, id: Long
            ) {
                currentType = types[position]
                applyFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?,
                position: Int, id: Long
            ) {
                currentTag = tags[position]
                applyFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        /* ---------------------------------------------------------
         * 🏷 Chip 필터 (플래너/인기/숙소/맛집 등)
         * --------------------------------------------------------- */
        val chipGroup = findViewById<ChipGroup>(R.id.chipCategory)

        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            currentType = when {
                checkedIds.contains(R.id.chipPlanner) -> "플래너"
                checkedIds.contains(R.id.chipPopular) -> "인기"   // (주의: 아직 카테고리에 인기 없음)
                checkedIds.contains(R.id.chipHotel) -> "숙소"
                checkedIds.contains(R.id.chipFood) -> "맛집"
                else -> "전체"
            }

            applyFilter()
        }

        /* ---------------------------------------------------------
         * 🔥 Firestore 전체 게시글 로드 → 이후 로컬 필터 적용
         * --------------------------------------------------------- */
        loadFromFirestore()
    }

    /**
     * Firestore에서 전체 게시글 불러오기
     */
    private fun loadFromFirestore() {
        db.collection("posts")
            .addSnapshotListener { snapshot, e ->

                if (snapshot == null || e != null) return@addSnapshotListener

                postList.clear()

                for (doc in snapshot.documents) {
                    val post = doc.toObject(Post::class.java) ?: continue
                    post.postId = doc.id

                    // 내 좋아요 여부 반영
                    val likedUsers = doc.get("likedUsers") as? List<String> ?: emptyList()
                    post.isLiked = uid != null && likedUsers.contains(uid)

                    postList.add(post)
                }

                applyFilter()
            }
    }

    /**
     * 🔎 필터 적용 (키워드 + 유형 + 태그)
     */
    private fun applyFilter() {

        val filtered = postList.filter { post ->

            // 1) 키워드 검사
            val matchKeyword =
                post.title.contains(currentKeyword, true) ||
                        post.content.contains(currentKeyword, true)

            // 2) 글 유형 검사
            val matchType =
                (currentType == "전체" || post.category == currentType)

            // 3) 태그 검사
            val matchTag =
                (currentTag == "전체" || post.category == currentTag)

            matchKeyword && matchType && matchTag
        }

        // 어댑터 갱신
        recyclerView.adapter = PostAdapter(filtered) { post ->
            val intent = Intent(this, CommunityPostDetailActivity::class.java)
            intent.putExtra("postData", post)
            startActivity(intent)
        }
    }
}
