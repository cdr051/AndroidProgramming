package com.example.cospicker.myinfo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.cospicker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 이름 수정 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 새 이름 입력
 *  - Firestore name 값 업데이트
 *  - 완료 Dialog 표시
 */
class EditNameActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_edit_name)

        /* ---------------------------------------------------------
         * 🔗 View 초기화
         * --------------------------------------------------------- */
        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnSave = findViewById<Button>(R.id.btn_save_name)
        val editName = findViewById<EditText>(R.id.edit_name)

        /* ---------------------------------------------------------
         * 🔙 닫기 버튼
         * --------------------------------------------------------- */
        btnClose.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 💾 저장 버튼 → Firestore 업데이트
         * --------------------------------------------------------- */
        btnSave.setOnClickListener {
            val newName = editName.text.toString().trim()

            // 입력값 검증
            if (newName.isEmpty()) {
                Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firestore 업데이트
            db.collection("users").document(uid)
                .update("name", newName)
                .addOnSuccessListener {
                    showSuccessDialog("이름이 변경되었습니다.") {
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "변경 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /* ---------------------------------------------------------
     * ✅ 변경 완료 Dialog
     * --------------------------------------------------------- */
    private fun showSuccessDialog(message: String, onClose: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("완료")
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> onClose() }
            .setCancelable(false)
            .show()
    }
}
