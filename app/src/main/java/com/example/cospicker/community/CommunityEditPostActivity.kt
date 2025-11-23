package com.example.cospicker.community

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.example.cospicker.community.model.Post
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 게시글 수정 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 기존 게시글 내용 불러오기
 *  - 제목/내용/카테고리 수정
 *  - Firestore 업데이트 후 화면 종료
 */
class CommunityEditPostActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community_edit_post)

        /* ---------------------------------------------------------
         * 📦 수정할 게시글 데이터 받기
         * --------------------------------------------------------- */
        post = intent.getSerializableExtra("postData") as? Post ?: return

        /* ---------------------------------------------------------
         * 🔗 View 연결
         * --------------------------------------------------------- */
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnEditDone = findViewById<TextView>(R.id.btnEditDone)
        val spinnerType = findViewById<Spinner>(R.id.spinnerPostType)
        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editContent = findViewById<EditText>(R.id.editContent)

        /* ---------------------------------------------------------
         * 📝 기존 게시글 내용 UI에 반영
         * --------------------------------------------------------- */
        editTitle.setText(post.title)
        editContent.setText(post.content)

        /* ---------------------------------------------------------
         * 📌 카테고리 스피너 설정
         * --------------------------------------------------------- */
        val types = listOf("일반글", "질문", "정보공유")

        spinnerType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        // 기존 카테고리 선택 상태로 설정
        spinnerType.setSelection(types.indexOf(post.category))

        /* ---------------------------------------------------------
         * 🔙 뒤로가기
         * --------------------------------------------------------- */
        btnBack.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * ⭐ 수정 완료 버튼
         * --------------------------------------------------------- */
        btnEditDone.setOnClickListener {

            val newTitle = editTitle.text.toString()
            val newContent = editContent.text.toString()
            val newCategory = spinnerType.selectedItem.toString()

            // ⚠️ 입력 검증
            if (newTitle.isBlank() || newContent.isBlank()) {
                Toast.makeText(this, "제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /* ---------------------------------------------------------
             * 🔥 Firestore 업데이트
             * --------------------------------------------------------- */
            db.collection("posts").document(post.postId)
                .update(
                    mapOf(
                        "title" to newTitle,
                        "content" to newContent,
                        "category" to newCategory
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "수정 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
