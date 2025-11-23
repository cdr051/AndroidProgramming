package com.example.cospicker.myinfo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 닉네임 수정 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 기존 닉네임 불러오기
 *  - 닉네임 편집
 *  - Firestore 업데이트
 *  - 성공 팝업 표시
 */
class EditNicknameActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⭐ 이름 편집 화면을 재사용 (레이아웃 재사용)
        setContentView(R.layout.myinfo_edit_name)

        /* ---------------------------------------------------------
         * 🔗 View 초기화
         * --------------------------------------------------------- */
        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnSave = findViewById<Button>(R.id.btn_save_name)
        val editNickname = findViewById<EditText>(R.id.edit_name)
        val title = findViewById<TextView>(R.id.title_edit_name)

        /* ---------------------------------------------------------
         * 📝 닉네임에 맞게 UI 문구 변경
         * --------------------------------------------------------- */
        title.text = "닉네임을 입력해주세요"
        editNickname.hint = "닉네임"

        /* ---------------------------------------------------------
         * 🔄 Firestore에서 현재 닉네임 불러오기
         * --------------------------------------------------------- */
        if (uid.isNotEmpty()) {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val currentNickname = doc.getString("nickname") ?: ""
                    if (currentNickname.isNotEmpty()) {
                        editNickname.setText(currentNickname)
                    }
                }
        }

        /* ---------------------------------------------------------
         * 🔙 닫기 버튼
         * --------------------------------------------------------- */
        btnClose.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 💾 저장 버튼 → Firestore 닉네임 업데이트
         * --------------------------------------------------------- */
        btnSave.setOnClickListener {

            val newNickname = editNickname.text.toString().trim()

            // 입력 검증
            if (newNickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uid.isEmpty()) {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firestore 업데이트
            db.collection("users").document(uid)
                .update("nickname", newNickname)
                .addOnSuccessListener {
                    showSuccessDialog()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "변경 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /* ---------------------------------------------------------
     * 🎉 닉네임 변경 완료 Dialog
     * --------------------------------------------------------- */
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("변경 완료")
            .setMessage("닉네임이 변경되었습니다.")
            .setPositiveButton("확인") { _, _ ->
                finish()    // MyInfoActivity로 복귀
            }
            .setCancelable(false)
            .show()
    }
}
