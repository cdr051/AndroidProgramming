package com.example.cospicker.community

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.example.cospicker.community.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

/**
 * 커뮤니티 글쓰기 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 제목/내용 작성
 *  - 글 카테고리 선택 (Spinner)
 *  - 사진 선택 + 미리보기
 *  - Firestore에 게시글 저장
 *  - (선택 시) Storage에 이미지 업로드 후 URL 저장
 */
class CommunityWritePostActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    private var imgUri: Uri? = null
    private val PICK_IMAGE = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community_write_post)

        /* ---------------------------------------------------------
         * 🔗 View 초기화
         * --------------------------------------------------------- */
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnSubmit = findViewById<TextView>(R.id.btnSubmit)

        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editContent = findViewById<EditText>(R.id.editContent)
        val spinnerType = findViewById<Spinner>(R.id.spinnerPostType)

        val btnPhoto = findViewById<ImageView>(R.id.btnPhoto)
        val imgPreview = findViewById<ImageView>(R.id.imgPreview)

        /* ---------------------------------------------------------
         * 🔙 뒤로가기
         * --------------------------------------------------------- */
        btnBack.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 📸 사진 선택
         * --------------------------------------------------------- */
        btnPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, PICK_IMAGE)
        }

        /* ---------------------------------------------------------
         * 📝 글쓰기 완료 버튼
         * --------------------------------------------------------- */
        btnSubmit.setOnClickListener {

            val title = editTitle.text.toString().trim()
            val content = editContent.text.toString().trim()
            val category = spinnerType.selectedItem?.toString() ?: "일반"

            // ⚠️ 입력 검증
            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이미지 있으면 Storage 업로드 후 게시글 업로드
            if (imgUri != null) {
                uploadImageThenPost(title, content, category)
            } else {
                uploadPost(title, content, category, imageUrl = null)
            }
        }
    }

    /* ---------------------------------------------------------
     * 🔥 (1) 이미지 업로드 → URL 받기 → 글 업로드
     * --------------------------------------------------------- */
    private fun uploadImageThenPost(title: String, content: String, category: String) {

        val filename = "community/${System.currentTimeMillis()}.jpg"
        val storage = FirebaseStorage.getInstance().reference.child(filename)

        storage.putFile(imgUri!!)
            .continueWithTask { storage.downloadUrl }
            .addOnSuccessListener { imageUrl ->
                uploadPost(title, content, category, imageUrl.toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }

    /* ---------------------------------------------------------
     * 🔥 (2) Firestore 게시글 업로드
     * --------------------------------------------------------- */
    private fun uploadPost(
        title: String,
        content: String,
        category: String,
        imageUrl: String?
    ) {

        // 시간 (문자열 포맷)
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val postData = hashMapOf(
            "userId" to uid,
            "nickname" to "닉네임",          // TODO: 실제 닉네임 불러오기
            "title" to title,
            "content" to content,
            "category" to category,
            "profileImage" to 0,
            "time" to time,
            "imageUrl" to (imageUrl ?: ""), // 이미지 없으면 빈 문자열
            "likeCount" to 0,
            "likedUsers" to emptyList<String>()
        )

        db.collection("posts")
            .add(postData)
            .addOnSuccessListener {
                Toast.makeText(this, "게시글이 등록되었습니다!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "오류: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /* ---------------------------------------------------------
     * 📸 사진 선택 후 미리보기
     * --------------------------------------------------------- */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imgPreview = findViewById<ImageView>(R.id.imgPreview)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imgUri = data?.data
            imgPreview.setImageURI(imgUri)
        }
    }
}
