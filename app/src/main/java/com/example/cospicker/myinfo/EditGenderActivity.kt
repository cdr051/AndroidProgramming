package com.example.cospicker.myinfo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.cospicker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 성별 수정 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 라디오 버튼(남성/여성)으로 성별 선택
 *  - Firestore gender 값 업데이트
 *  - 저장 완료 Dialog 표시
 */
class EditGenderActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_edit_gender)

        /* ---------------------------------------------------------
         * 🔗 View 초기화
         * --------------------------------------------------------- */
        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnSave = findViewById<Button>(R.id.btn_save_gender)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group_gender)

        /* ---------------------------------------------------------
         * 🔙 닫기 버튼
         * --------------------------------------------------------- */
        btnClose.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 💾 저장 버튼 → Firestore 성별 업데이트
         * --------------------------------------------------------- */
        btnSave.setOnClickListener {

            // 선택된 라디오 버튼 값 가져오기
            val gender = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_male -> "남성"
                R.id.radio_female -> "여성"
                else -> ""
            }

            // Firestore 업데이트
            db.collection("users").document(uid)
                .update("gender", gender)
                .addOnSuccessListener {
                    showSuccessDialog("성별이 변경되었습니다.") {
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
